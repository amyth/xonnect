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

    public void createVertexes(def vertexes) {
        println "Preparing vertices."
        vertexes.row.each {
            try{
                def uniqueLabel = "candidate"
                it.each {
                    def newVertex = this.graph.addVertex(label, uniqueLabel)
                    newVertex.property("uid", it.uid[0])
                    newVertex.property("name", it.name[0])
                    newVertex.property("title", it.title[0])
                    // println "(UID::: " +  it.uid[0] + ") (name::: " +  it.name[0] + ") (title ::: " + it.title[0] +")"
                      
                }
            } catch(SchemaViolationException e){
                println "NOT ADDING UNIQUE EXCEPTION:::   " + it.toString()
            }
        }
        this.graph.tx().commit()
        println "Created vertices successfully"
    }

    public void populate() {
          // Create vertexes from the given dummy data
          def vertexTransaction = this.graph.newTransaction()
          def vertexes = this.dummyData.results.data;
          this.createVertexes(vertexes)
          vertexTransaction.commit()
          this.initGraph()
          println "Candidate  Nodes population successfully accomplished. Please hit Ctrl+C to exit." 
    }


    public void initialize(String jsonPath) {
         String fileContents = new File(jsonPath).getText('UTF-8')
         def slurper = new JsonSlurper()
         def results = slurper.parseText(fileContents)
         this.dummyData = results;
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
graphBuilder.main("/Users/admin/Documents/scripts/json_data/candidate.json", "conf/janusgraph-cassandra.properties")

