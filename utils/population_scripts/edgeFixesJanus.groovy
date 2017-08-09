import groovy.json.JsonSlurper;
import java.util.ArrayList;
import java.util.Random;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.Multiplicity;
import org.janusgraph.core.schema.SchemaAction;
import org.janusgraph.core.schema.SchemaStatus;
import org.janusgraph.core.util.JanusGraphCleanup;
import org.janusgraph.graphdb.database.StandardJanusGraph;
import org.janusgraph.graphdb.database.management.ManagementSystem;

/**
 * Given a json file, populates data into the given JanusGraph DB
 */
class JanusGraphBuilder {

    String graphPath;
    int transactionStep = 20000;
    StandardJanusGraph graph;
    ManagementSystem management;
    GraphTraversalSource traversal;

    def dummyData;


    public void main(String jsonPath, String janusGraphPath) {
        this.graphPath  = janusGraphPath
        this.initGraph()
        this.initialize(jsonPath)
        this.populate()
    }

    public void createEdges(def uid, def connections) {
        println "Preparing " + connections.size() + " edges for uid " + uid
        def owner = this.traversal.V().has("uid", uid).next()
        connections.each {
            def conn = this.traversal.V().has("uid", it).next()
            owner.addEdge("knows", conn)
        }
        println "Created edges successfully"
    }

    public void populate() {

        def edgeTransaction = this.graph.newTransaction()
        def vertexes = this.dummyData.vertexes;
        def connLabels = ['person', 'candidate', 'recruiter', 'employee']
        def prospective = []

        // Create a prospective connections list
        vertexes.each {
            if (connLabels.contains(it.labels[0])) {
                prospective.add(it)
            }
        }

        prospective.each {
            def connections = []
            def added = 0
            def minC = 100
            def maxC = 150
            def r = new Random()
            def required = r.nextInt(maxC - minC) + minC

            // Prepare connections
            while (added < required) {
                def index = r.nextInt(prospective.size() - 1) + 1
                println prospective[index]
                def cUid = prospective[index].properties.uid
                connections.add(cUid);
                added ++;
            }

            this.createEdges(it.properties.uid, connections);
        }
        edgeTransaction.commit()
        this.graph.tx().commit()
    }

    public void initialize(String jsonPath) {
        String fileContents = new File(jsonPath).getText('UTF-8')
        def slurper = new JsonSlurper()
        def results = slurper.parseText(fileContents)
        this.dummyData = results;
    }

    public void initGraph() {
        this.graph = JanusGraphFactory.open(this.graphPath)
        this.traversal = this.graph.traversal()
    }
}


JanusGraphBuilder graphBuilder = new JanusGraphBuilder()
graphBuilder.main("/tmp/dummy.json", "conf/testconf.properties")
