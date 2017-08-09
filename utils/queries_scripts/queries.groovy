import java.util.ArrayList;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.graphdb.database.StandardJanusGraph;
import org.janusgraph.graphdb.database.management.ManagementSystem;
import org.apache.tinkerpop.gremlin.structure.T;

/**
 * Queries for load testing of JanusGraph DB
 */
class CreateUserQuery {

    String graphPath;
    StandardJanusGraph graph;
    ManagementSystem management;
    GraphTraversalSource traversal;
    def personVertex;
    def phoneVertex;
    def emailVertex;


   public void main(String janusGraphPath, String UID, String Phone, String Email) {
        this.graphPath  = janusGraphPath
        this.initGraph()
        this.getOrCreateVertex(UID, Phone, Email)
        this.initGraph()
        this.getOrCreateEdges(UID, Phone, Email)
    }

    public void initGraph() {
        println ("Initializing graph ...")
        this.graph = JanusGraphFactory.open(this.graphPath)
        this.traversal = this.graph.traversal()
        println ("Graph ready.")
    }

    public void getOrCreateVertex(String uid, String Phone, String Email) {
        def vertex
        //find all vertexes first
        def UIDvertexSet = this.traversal.V().has("uid", uid)
        def UIDvertexExists = UIDvertexSet.hasNext()

        def PhoneVertexSet = this.traversal.V().has("number", Phone)
        def PhoneVertexExists = PhoneVertexSet.hasNext()

        def EmailVertexSet = this.traversal.V().has("email", Email)
        def EmailVertexExists = EmailVertexSet.hasNext()

        //get or create person
        String  personLabel = "person"
        if (UIDvertexExists) {
            println UIDvertexExists
            personVertex = UIDvertexSet.next()
            println personVertex.value('name')
        } else{
            // Create person vertex 
            def vertexTransaction = this.graph.newTransaction()
            personVertex = this.createVertexes(personLabel,"uid",  uid)
            vertexTransaction.commit()
            this.initGraph()
            println "vertex created with:::: "+ uid 
        }

        println(personVertex)

        //get or create Phone
        String phLabel = "phone"       
        if (PhoneVertexExists) {
            println PhoneVertexExists
            phoneVertex = PhoneVertexSet.next()
            println phoneVertex.value('number')
        } else{
            // Create phone  vertex 
            def vertexTransaction = this.graph.newTransaction()
            phoneVertex = this.createVertexes(phLabel,"number", Phone)
            vertexTransaction.commit()
            this.initGraph()
            println "vertex created with:::: " + Phone
        }

       //get or create Email 
        String emailLabel = "email"
        if (EmailVertexExists) {
            println EmailVertexExists
            emailVertex = EmailVertexSet.next()
            println emailVertex.value('email')
        } else{
            // Create email  vertex 
            def vertexTransaction = this.graph.newTransaction()
            emailVertex = this.createVertexes(emailLabel,"email",  Email)
            vertexTransaction.commit()
            this.initGraph()
            println "vertex created with:::: " + Email
        }
    }
    
    
    public void getOrCreateEdges(String uid, String Phone, String Email) {

        //check if phone number edge is present
        if(this.traversal.V(personVertex).outE("has").inV().has("number", Phone).hasNext()){
            println "Phone Number already present"
        }else{
            //TO-DO:::create phone edge here
           def e =  this.createEdges(uid, "number", Phone)
           println e
           println "phone edge created"
        } 

        //check if email edge is present    
        if(this.traversal.V(personVertex).outE("has").inV().has("email", Email).hasNext()){
            println "Email already present"
        }else{
            //TO-DO:::create email edge here
            def ed = this.createEdges(uid, "email", Email)
            println ed
            println "email edge created"
        }     
    }

    public def createVertexes(String labelValue, String propertyKey,  String propertyValue) {
        println "Creating vertex::::" + labelValue 
        def newVertex = this.graph.addVertex(T.label, labelValue)
        newVertex.property(propertyKey, propertyValue)
        this.graph.tx().commit()
        println "Created vertices successfully" + newVertex
        return newVertex
    }
    
    public void createEdges(String uid, String key, String value) {
        println "Preparing edges."
        
        def relation = "has"
        def vertexSetFrom = this.traversal.V().has("uid", uid)
        def vertexFrom = vertexSetFrom.next()
        def vertexSetTo = this.traversal.V().has(key, value)
        def vertexTo = vertexSetTo.next()
        def newEdge = vertexFrom.addEdge(relation, vertexTo)
            
       
        this.graph.tx().commit()
        println "Created edges successfully"
    }

}


CreateUserQuery graphQuery = new CreateUserQuery ()
graphQuery.main("conf/testconf.properties","01bfde92998746c4a6dc95a960b17e01","+91999903939", "amanda_miller@yahoo.co.in" )
