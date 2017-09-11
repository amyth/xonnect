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
 * creates fresh graph with xonnect schema
 */
 
class JanusGraphBuilder {
    String graphPath;
    StandardJanusGraph graph;
    ManagementSystem management;
    GraphTraversalSource traversal;

    public void main(String janusGraphPath) {
        this.graphPath  = janusGraphPath
        this.initGraph()
        this.reset()
        this.createSchema()
    }


    public void createSchema() {
        println "Preparing schema."
        // Do not create indexes while another transaction is in progress
        this.graph.tx().rollback()
        this.management = this.graph.openManagement()

        // Make property keys
        def uid = this.management.makePropertyKey("uid").dataType(String.class).make()
        def name = this.management.makePropertyKey("name").dataType(String.class).make()
        def title = this.management.makePropertyKey("title").dataType(String.class).make()
        def email = this.management.makePropertyKey("email").dataType(String.class).make()
        def number = this.management.makePropertyKey("number").dataType(String.class).make()
        def score = this.management.makePropertyKey("score").dataType(String.class).make()
        def linkedinId  = this.management.makePropertyKey("linkedin_id").dataType(String.class).make()
        def linkedinUrl = this.management.makePropertyKey("linkedin_url").dataType(String.class).make()
        def imageUrl = this.management.makePropertyKey("image_url").dataType(String.class).make()
        def companyId = this.management.makePropertyKey("company_id").dataType(String.class).make()
        def instituteId = this.management.makePropertyKey("institute_id").dataType(String.class).make()
        def jobId = this.management.makePropertyKey("job_id").dataType(String.class).make()
        def instituteName = this.management.makePropertyKey("institute_name").dataType(String.class).make()
        def companyName = this.management.makePropertyKey("company_name").dataType(String.class).make()
        println "Properties created (not commited)"

        // Define Vertex Labels
        this.management.makeVertexLabel("person").make();
        def candidateV = this.management.makeVertexLabel("candidate").make();
        def recruiterV = this.management.makeVertexLabel("recruiter").make();
        def employeeV = this.management.makeVertexLabel("employee").make();
        def linkedinV = this.management.makeVertexLabel("linkedin").make();
        def jobV = this.management.makeVertexLabel("job").make();
        def companyV =this.management.makeVertexLabel("company").make();
        def instituteV = this.management.makeVertexLabel("institute").make();
        def phoneV = this.management.makeVertexLabel("phone").make();
        def emailV = this.management.makeVertexLabel("email").make();
        println "Vertex Labels (not commited)"

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
        println "Edge Labels (not commited)"

        // Create indexes
        this.management.buildIndex('uniqueUid', Vertex.class).addKey(uid).unique().buildCompositeIndex()
        this.management.buildIndex('uniqueEmail', Vertex.class).addKey(email).indexOnly(emailV).unique().buildCompositeIndex()
        this.management.buildIndex('uniqueNumber', Vertex.class).addKey(number).indexOnly(phoneV).unique().buildCompositeIndex()
        this.management.buildIndex('uniqueJobId', Vertex.class).addKey(jobId).indexOnly(jobV).unique().buildCompositeIndex()
        this.management.buildIndex('uniqueRecruiterId', Vertex.class).addKey(jobId).indexOnly(jobV).unique().buildCompositeIndex()
        this.management.buildIndex('uniqueComapnyId', Vertex.class).addKey(jobId).indexOnly(jobV).unique().buildCompositeIndex()
        this.management.buildIndex('uniqueInstituteId', Vertex.class).addKey(jobId).indexOnly(jobV).unique().buildCompositeIndex()
        println "Indexes prepared"
        this.management.commit()
        println "Created schema successfully"

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
graphBuilder.main( "conf/janusgraph-cassandra.properties")

