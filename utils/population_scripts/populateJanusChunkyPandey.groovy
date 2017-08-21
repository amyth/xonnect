import groovy.json.JsonSlurper;
import java.util.ArrayList;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.Multiplicity;
import org.janusgraph.core.schema.SchemaAction;
import org.janusgraph.core.schema.SchemaStatus;
import org.janusgraph.core.util.JanusGraphCleanup;
import org.janusgraph.graphdb.database.StandardJanusGraph;
import org.janusgraph.graphdb.database.management.ManagementSystem;
import org.janusgraph.core.schema.ConsistencyModifier;

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

        println "Preparing schema."
        // Do not create indexes while another transaction is in progress
        this.graph.tx().rollback()
        this.management = this.graph.openManagement()
        //this.management.set('ids.block-size', 20000000)

        // Make property keys
        def uid = this.management.makePropertyKey("uid").dataType(String.class).make()
        def name = this.management.makePropertyKey("name").dataType(String.class).make()
        def number = this.management.makePropertyKey("number").dataType(String.class).make()
        def email = this.management.makePropertyKey("email").dataType(String.class).make()
        def score = this.management.makePropertyKey("score").dataType(Float.class).make()
        def linkedinId = this.management.makePropertyKey("linkedin_id").dataType(String.class).make()
        def linkedinUrl = this.management.makePropertyKey("profile_url").dataType(String.class).make()
        def imageUrl = this.management.makePropertyKey("image_url").dataType(String.class).make()
        def instituteName = this.management.makePropertyKey("institute_name").dataType(String.class).make()
        def companyName = this.management.makePropertyKey("company_name").dataType(String.class).make()
        def jobId = this.management.makePropertyKey("job_id").dataType(String.class).make()

        def phoneV = this.management.makeVertexLabel("phone").make();
        def emailV = this.management.makeVertexLabel("email").make();


        // Create indexes
        def uniqueUID = this.management.buildIndex('uniqueUid', Vertex.class).addKey(uid).unique().buildCompositeIndex()
        this.management.setConsistency(uid, ConsistencyModifier.LOCK) // Ensures only one name per vertex
        this.management.setConsistency(uniqueUID, ConsistencyModifier.LOCK) // Ensures name uniqueness in the graph
        this.management.commit()
        this.management = this.graph.openManagement()
        def uniqueEmail = this.management.buildIndex('uniqueEmail', Vertex.class).addKey(email).indexOnly(emailV).unique().buildCompositeIndex()
        this.management.setConsistency(email, ConsistencyModifier.LOCK) // Ensures only one name per vertex
        this.management.setConsistency(uniqueEmail, ConsistencyModifier.LOCK) // Ensures name uniqueness in the graph
        this.management.commit()
        this.management = this.graph.openManagement()
        def uniqueNum = this.management.buildIndex('uniqueNumber', Vertex.class).addKey(number).indexOnly(phoneV).unique().buildCompositeIndex()
        this.management.setConsistency(number, ConsistencyModifier.LOCK) // Ensures only one name per vertex
        this.management.setConsistency(uniqueNum, ConsistencyModifier.LOCK) // Ensures name uniqueness in the graph
        this.management.commit()
        this.management.awaitGraphIndexStatus(this.graph, 'uniqueUid').call()
        this.management = this.graph.openManagement()
        this.management.updateIndex(this.management.getGraphIndex('uniqueUid'), SchemaAction.REINDEX).get()

        // Define Edge Labels
        this.management.makeEdgeLabel("knows").make();
        this.management.makeEdgeLabel("has").make();
        this.management.makeEdgeLabel("provided_by").make();
        this.management.makeEdgeLabel("studied_at").make();
        this.management.makeEdgeLabel("worked_at").make();
        this.management.makeEdgeLabel("posted").make();
        this.management.makeEdgeLabel("liked").make();
        this.management.makeEdgeLabel("worked_with").make();
        this.management.makeEdgeLabel("studied_with").make();
        this.management.makeEdgeLabel("is_a_match_for").make();

        // Define Vertex Labels
        this.management.makeVertexLabel("person").make();
        this.management.makeVertexLabel("candidate").make();
        this.management.makeVertexLabel("recruiter").make();
        this.management.makeVertexLabel("employee").make();
        this.management.makeVertexLabel("linkedin").make();
        this.management.makeVertexLabel("job").make();
        this.management.makeVertexLabel("company").make();
        this.management.makeVertexLabel("institute").make();
        this.management.commit()

        println "Created schema successfully"
    }

    public void populate() {
        // Create db schema
        //this.createSchema()

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
        //this.resetData()
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
graphBuilder.main("/tmp/dummy.json", "conf/testconf.properties")
