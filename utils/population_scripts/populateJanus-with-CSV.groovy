import com.opencsv.CSVReader;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.janusgraph.core.SchemaViolationException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Given a json file, populates data into the given JanusGraph DB
 */
 
class JanusGraphBuilder {
    String graphPath;
    StandardJanusGraph graph;
    ManagementSystem management;
    GraphTraversalSource traversal;

    
    JSONObject dummyData;


    public void main(String jsonPath, String janusGraphPath) {
        this.graphPath  = janusGraphPath
        this.initGraph()
        this.initialize(jsonPath)
        this.populate()
    }

    public void createEdges(def edges) {
        println "Preparing edges."
        edges.each {
            println it.edge
            def relation = it.edge
            println it.properties
            def properties = it.properties
            def vertexFrom = this.traversal.V().has("uid", it.nodes[0])[0]
            def vertexTo = this.traversal.V().has("uid", it.nodes[1])[0]
            println "From : " +  it.nodes[0]
            println "To : " + it.nodes[1]
            
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
            try{
                println "Label: " + it.labels[0]
                def uniqueLabel = it.labels[0]
                def properties = it.properties
                def newVertex = this.graph.addVertex(label, uniqueLabel)
                properties.each {
                     println "Key: " + it.key + "     Value: " + it.value 
                    newVertex.property(it.key, it.value)
                }
            } catch(SchemaViolationException e){
                println "NOT ADDING UNIQUE EXCEPTION:::   " + it.toString()
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

    public void populate() {
//        // Create db schema
//        try {
//            this.createSchema()
//        } catch (Exception e) {
//            println e.toString();
//            e.printStackTrace();
//        }
//
//        // Create vertexes from the given dummy data
//        def vertexTransaction = this.graph.newTransaction()
//        def vertexes = this.dummyData.vertexes;
//        this.createVertexes(vertexes)
//        vertexTransaction.commit()
//        this.initGraph()
          println "populating edges"
          def edgeTransaction = this.graph.newTransaction()
          // Create edges from the given dummy data
          def edges = this.dummyData.edges;
          this.createEdges(edges)
          edgeTransaction.commit()
          this.initGraph()

        println "Graph population successfully accomplished. Please hit Ctrl+C to exit." 
    }


    public void initialize(String csvFilePath) {

        //CSVReader reader = new CSVReader(new FileReader(csvFilePath), ',', '"', '|'); 
        char c = ','
        char s = '\"'
        char j = "|"
        CSVReader reader = new CSVReader(new FileReader(csvFilePath), c, s, j);
        this.dummyData = new JSONObject();  
        JSONArray VertexArray = new JSONArray();
        JSONArray EdgeArray = new JSONArray();
        def record = [];
        def position = 0;
        reader.readNext()
        while ((record = reader.readNext()) != null) {

            //Check csv record is a vertices or an edge based on start column value            
            if (record[15].size() == 0 ){
           
//                JSONObject vertex = new JSONObject();
//                JSONArray labels = new JSONArray();
//                
//                //add labels
//                def label_name = record[1].toString()
//                
//                if (label_name.size() > 0) {
//                    label_name= label_name.substring(1, label_name.size())
//                    println(record[0] + "    >   " + label_name)
//               
//
//                    labels.add(label_name)
//                    vertex.put("labels" , labels);
//
//                     //add properties
//                    JSONObject properties = new JSONObject();
//                    if(record[2].size()>0) { properties.put("name", record[2]); }
//                    if(record[3].size()>0) { properties.put("uid", record[3]); }
//                    if(record[4].size()>0) { properties.put("title", record[4]); }
//                    if(record[6].size()>0) { properties.put("number", record[6]); }
//                    if(record[5].size()>0) { properties.put("email", record[5]); }
//                    if(record[7].size()>0) { properties.put("image_url", record[7]); }
//                    if(record[8].size()>0) { properties.put("linkedin_url", record[8]); }
//                    if(record[9].size()>0) { properties.put("linkedin_id" , record[9]); }
//                    if(record[10].size()>0) { properties.put("version", record[10]); }
//                    if(record[11].size()>0) { properties.put("phone", record[11]); }
//                    if(record[12].size()>0) { properties.put("age", record[12]); }
//                
//                    vertex.put("properties", properties);
//                
//                    //add new vertex
//                    VertexArray.add( vertex);
             }
             else{
                       //extract edges information from CSV
                       JSONObject edge_jobj = new JSONObject();
                       JSONArray nodes = new JSONArray();
        
                       //add edge labels
                       def edge_label = record[17].toString()

                       if (edge_label.size() > 0) {

                          edge_jobj.put("edge", edge_label);
                          String From = record[15]
                          String To = record[16]
                          nodes.add(From);
                          nodes.add(To);
                          edge_jobj.put("nodes" , nodes);

                          //add properties
                          JSONObject eProperties = new JSONObject();
                          if(record[20].size()>0) { eProperties.put("name", record[20]); }
                          if(record[21].size()>0) { eProperties.put("score", record[21]); }

                          edge_jobj.put("properties", eProperties);

                          //add new vertex
                          EdgeArray.add(edge_jobj);
                          //println "From:: " + record[15] + " TO:::" + record[16] + " Name: " + record[20] + " Score:::" + record[21]
                       }   
            }

            position++;
        }

        //add all vertexes
        this.dummyData.put("vertexes", VertexArray);
        //add all edges
        this.dummyData.put("edges", EdgeArray);
        //this.resetData()     
    }
    public truncateSpecialChars(String text){
        Pattern pt = Pattern.compile("[^a-zA-Z0-9]");
        Matcher match= pt.matcher(c);
        while(match.find())
        {
            String s= match.group();
        c=c.replaceAll("\\"+s, "");
        }
        println("String Replaced:::::::::::::::::::::::::::::::::::::::::::::::::::" + c);
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
graphBuilder.main("/Users/admin/Desktop/hello.csv", "conf/janusgraph-cassandra.properties")

