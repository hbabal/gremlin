import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.hasNot;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.out;
import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.select;

import java.io.File;
import java.io.IOException;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

public class Main {

    public static void main(String[] args) throws IOException {
        ClassLoader classLoader = Main.class.getClassLoader();
        File file = new File(classLoader.getResource("QuickSearch.json").getFile());
        String dbPath = file.getAbsolutePath();
        Graph graph = TinkerGraph.open();
        graph.io(IoCore.graphson()).readGraph(dbPath);

        System.out.println(graph.traversal().V().hasLabel("TypeDeclaration").as("mainClass")
                .out("DEFINE").hasLabel("MethodDeclaration").as("mainMethod")
                .repeat(out("CALL", "CALL_NONDYNAMIC"))
                .until(hasNot("label")).as("methodOnThePath")
                .select("methodOnThePath").values("longname")
                .project("c").by(select("methodOnThePath").in("DEFINE").values("longname")).dedup()
                .group().by(select("mainClass").values("longname"))
                .toList());

    }

}
