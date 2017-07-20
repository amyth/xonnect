import groovy.json.JsonSlurper;
import java.util.ArrayList;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.PropertyKey;
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

    public void createEdges(def edges) {
        println "Preparing edges."
        edges.each {
            def relation = it.edge
            def properties = it.properties
            println this.traversal.V()
            println it.nodes[0]
            def vertexFrom = this.traversal.V().has("uid", it.nodes[0])[0]
            def vertexTo = this.traversal.V().has("uid", it.nodes[1])[0]
            def newEdge = vertexFrom.addEdge(relation, vertexTo)
            properties.each {
                if (it.key == 'score') {
                    it.value = Float.parseFloat(it.value.toString())
                }
                newEdge.property(it.key, it.value)
            }
        }
        this.graph.tx().commit()
        println "Created edges successfully"
    }

    public void createVertexes(def vertexes) {
        println "Preparing vertices."
        vertexes.each {
            def uniqueLabel = it.labels[0]
            def properties = it.properties
            def newVertex = this.graph.addVertex(label, uniqueLabel)
            properties.each {
                newVertex.property(it.key, it.value)
            }
        }
        this.graph.tx().commit()
        println "Created vertices successfully"
    }

    public void createSchema() {
        println "Preparing schema."
        // Do not create indexes while another transaction is in progress
        this.graph.tx().rollback()
        this.management = this.graph.openManagement()

        // Make property keys
        def uid = this.management.makePropertyKey("uid").dataType(String.class).make()
        def name = this.management.makePropertyKey("name").dataType(String.class).make()
        def number = this.management.makePropertyKey("number").dataType(String.class).make()
        def email = this.management.makePropertyKey("email").dataType(String.class).make()
        def score = this.management.makePropertyKey("score").dataType(Float.class).make()
        def linkedinId = this.management.makePropertyKey("linkedinId").dataType(String.class).make()
        def linkedinUrl = this.management.makePropertyKey("linkedinUrl").dataType(String.class).make()
        def imageUrl = this.management.makePropertyKey("imageUrl").dataType(String.class).make()

        // Create indexes
        this.management.buildIndex('uniqueUid', Vertex.class).addKey(uid).unique().buildCompositeIndex()
        this.management.commit()
        this.management.awaitGraphIndexStatus(this.graph, 'uniqueUid').call()
        this.management = this.graph.openManagement()
        this.management.updateIndex(this.management.getGraphIndex('uniqueUid'), SchemaAction.REINDEX).get()
        this.management.commit()
        
        println "Created schema successfully"
    }

    public void populate() {
        // Create db schema
        this.createSchema()

        // Create vertexes from the given dummy data
        def vertexTransaction = this.graph.newTransaction()
        def vertexes = this.dummyData.vertexes;
        this.createVertexes(vertexes)
        vertexTransaction.commit()
        this.initGraph()

        def edgeTransaction = this.graph.newTransaction()
        // Create edges from the given dummy data
        def edges = this.dummyData.edges;
        this.createEdges(edges)
        edgeTransaction.commit()
        this.initGraph()

        println "Graph population successfully accomplished. Please hit Ctrl+C to exit." 
    }

    public void initialize(String jsonPath) {
        String fileContents = new File(jsonPath).getText('UTF-8')
        def slurper = new JsonSlurper()
        def results = slurper.parseText(fileContents)
        this.dummyData = results;
        this.resetData()
    }

    public void resetData() {
        // Remove all the data from the storage backend
        this.graph.close()
        JanusGraphCleanup.clear(this.graph)
        this.initGraph()
    }

    public void initGraph() {
        this.graph = JanusGraphFactory.open(this.graphPath)
        this.traversal = this.graph.traversal()
    }
}


JanusGraphBuilder graphBuilder = new JanusGraphBuilder()
graphBuilder.main("/tmp/dummy.json", "conf/janusgraph-cassandra.properties")
