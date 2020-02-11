package pl.archanalysis;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaSource;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.LinkSource;
import guru.nidi.graphviz.model.Node;
import org.apache.commons.lang3.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static guru.nidi.graphviz.attribute.Rank.RankDir.TOP_TO_BOTTOM;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;

public class ArchAnalysis {

    public static void main(String... args) throws IOException {
        String codePath = "pl.demo";
//        Reflections reflections = new Reflections(codePath, new SubTypesScanner(false));
//
//        Set<Class<? extends Object>> allClasses =
//                reflections.getSubTypesOf(Object.class);

        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceTree(new File("D:\\Programowanie\\archanalysis\\src\\main\\java\\" + codePath.replace(".", "\\")));
        List<PackageAnalysis> packageAnalyses = builder.getSources().stream()
                .map(javaSource -> createPackageAnalysis(javaSource, codePath))
                .collect(Collectors.toList());
//                .forEach(System.out::println);
//        builder.addSourceFolder( new File( "src/main/java" ) );
//        JavaModule javaModule = builder.addSourceFolder(new File("D:\\Programowanie\\archanalysis\\src\\main\\java"));
//        JavaSource javaSource = builder.addSource(new FileReader("D:\\Programowanie\\archanalysis\\src\\main\\java\\pl\\demo\\Demo.java"));
//        System.out.println(javaSource.getImports());
//        javaModule.getClasses()
//                .forEach(javaClass -> System.out.println(javaClass));
        List<LinkSource> linkSources = packageAnalyses.stream()
                .map(packageAnalysis -> linkNodes(
                        packageAnalysis.getPackageName(),
                        packageAnalysis.getPackageDependencies(),
                        4))
                .collect(Collectors.toList());

        Graph g = graph("example1").directed()
                .graphAttr()
                .with(Rank.dir(TOP_TO_BOTTOM))
                .with(
                        linkSources
//                        node("a").with(Color.RED).link(node("b")),
//                        node("b").link(to(node("c")).with(Style.DASHED))
                );
        Graphviz.fromGraph(g).render(Format.PNG).toFile(new File("example/ex1.png"));

    }

    private static Node linkNodes(String packageName, Set<String> packageDependencies, int level) {
        Node node = node(levelNode(level, packageName));
        List<Node> links = packageDependencies.stream()
                .map(dep -> node(levelNode(level, dep)))
                .collect(Collectors.toList());
        return node.link(links);
    }

    private static String levelNode(int level, String dep) {
        String[] split = dep.split("\\.");
        return Stream.of(split)
                .limit(level)
                .collect(Collectors.joining("."));
    }

    private static PackageAnalysis createPackageAnalysis(JavaSource javaSource, String codePath) {
        return new PackageAnalysis(
                javaSource.getPackageName(),
                javaSource.getImports().stream()
                        .filter(s -> s.startsWith(codePath))
                        .map(ClassUtils::getPackageCanonicalName)
                        .collect(Collectors.toSet())
        );
    }
}
