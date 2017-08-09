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

/**
 * Given a json file, populates data into the given JanusGraph DB
 */
class JanusGraphIndexing {

    String graphPath;
    StandardJanusGraph graph;
    ManagementSystem management;
    GraphTraversalSource traversal;

    public void main(String indexName, String janusGraphPath) {
        this.graphPath  = janusGraphPath
        this.initGraph()
        this.removeIndex(indexName)
        this.reIndex(indexName)
    }

    public void reIndex(String indexName) {
        println "Preparing schema."
        // Do not create indexes while another transaction is in progress
        this.graph.tx().rollback()
        this.management = this.graph.openManagement()

        // Make property keys
        def index = this.management.getPropertyKey(indexName).dataType(String.class).make()

        // Create indexes
        this.management.buildIndex(indexName, Vertex.class).addKey(index).unique().buildCompositeIndex()
        this.management.commit()
        this.management.awaitGraphIndexStatus(this.graph, indexName).call()
        this.management = this.graph.openManagement()
        this.management.updateIndex(this.management.getGraphIndex(indexName), SchemaAction.REINDEX).get()

        println "Reindexed Successfully!"
    }

    public void removeIndex(indexName) {

    // Disable the "name" composite index
        this.management = this.graph.openManagement()
        def nameIndex = this.management.getGraphIndex(indexName)
        this.management.updateIndex(nameIndex, SchemaAction.DISABLE_INDEX).get()
        this.management.commit()
        this.graph.tx().commit()

    // Block until the SchemaStatus transitions from INSTALLED to REGISTERED
        ManagementSystem.awaitGraphIndexStatus(graph, indexName).status(SchemaStatus.DISABLED).call()

    // Delete the index using JanusGraphManagement
        this.management = this.graph.openManagement()
        def delIndex = this.management.getGraphIndex(indexName)
        def future = this.management.updateIndex(delIndex, SchemaAction.REMOVE_INDEX)
        this.management.commit()
        this.graph.tx().commit()
    }


a   public void initGraph() {
        this.management.updateIndex(this.management.getGraphIndex('uniqueEdgeUid'), SchemaAction.REINDEX).get()
        thisnsactions().size().management.updateIndex(this.management.getGraphIndex('uniqueEdgeUid'), SchemaAction.REINDEX).get()
        this.management.updateIndex(this.management.getGraphIndex('uniqueEdgeUid'), SchemaAction.REINDEX).get()
        this.graph = JanusGraphFactory.open(this.graphPath)
        this.traversal = this.graph.traversal()
    }
}


JanusGraphIndexing  graphIndexing = new JanusGraphIndexing()
graphIndexing.main("number", "conf/testconf.properties")
