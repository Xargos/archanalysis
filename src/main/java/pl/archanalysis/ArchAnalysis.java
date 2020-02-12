package pl.archanalysis;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaSource;
import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.LinkSource;
import guru.nidi.graphviz.model.Node;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import org.apache.commons.lang3.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static guru.nidi.graphviz.attribute.Rank.RankDir.TOP_TO_BOTTOM;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;

public class ArchAnalysis {

    public static void analyze(String codePackage, String sourcePath, String pathSeparator) throws IOException {
        int level = codePackage.split("\\.").length + 1;
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceTree(new File(sourcePath + codePackage.replace(".", pathSeparator)));
        List<PackageAnalysis> packageAnalyses = builder.getSources().stream()
                .map(javaSource -> createPackageAnalysis(javaSource, codePackage, level))
                .reduce(HashMap.empty(), PackageAnalysis::merge, HashMap::merge)
                .toJavaStream()
                .map(Tuple2::_2)
                .collect(Collectors.toList());

        CircularDependencyAnalyzer circularDependencyAnalyzer = new CircularDependencyAnalyzer();
        packageAnalyses = circularDependencyAnalyzer.analyze(packageAnalyses);

        List<LinkSource> linkSources = packageAnalyses.stream()
                .map(packageAnalysis -> linkNodes(
                        packageAnalysis.getPackageName(),
                        packageAnalysis.getPackageDependencies()))
                .collect(Collectors.toList());

        Graph g = graph("example1").directed()
                .graphAttr()
                .with(Rank.dir(TOP_TO_BOTTOM))
                .with(linkSources);
        Graphviz.fromGraph(g).render(Format.PNG).toFile(new File("example/ex1.png"));
    }

    private static Node linkNodes(String packageName, List<PackageDependency> packageDependencies) {
        List<Link> links = packageDependencies.stream()
                .map(ArchAnalysis::buildLink)
                .collect(Collectors.toList());
        return node(packageName).link(links);
    }

    private static Link buildLink(PackageDependency dep) {
        return Link.to(node(dep.getName()))
                .with(Label.of(dep.getCount().toString()))
                .with(dep.isCircular() ? Color.RED : Color.BLACK);
    }

    private static PackageAnalysis createPackageAnalysis(JavaSource javaSource, String codePath, int level) {
        return new PackageAnalysis(
                levelNode(level, javaSource.getPackageName()),
                javaSource.getImports().stream()
                        .filter(s -> s.startsWith(codePath))
                        .map(ClassUtils::getPackageCanonicalName)
                        .map(s -> levelNode(level, s))
                        .reduce(HashMap.empty(), ArchAnalysis::merge, HashMap::merge)
                        .toJavaStream()
                        .map(params -> PackageDependency.builder()
                                .name(params._1())
                                .count(params._2())
                                .build())
                        .collect(Collectors.toList()));
    }

    private static HashMap<String, Integer> merge(HashMap<String, Integer> map, String name) {
        return map.put(name,
                map.get(name)
                        .map(count -> count + 1)
                        .getOrElse(() -> 1));
    }

    private static String levelNode(int level, String dep) {
        String[] split = dep.split("\\.");
        return Stream.of(split)
                .limit(level)
                .collect(Collectors.joining("."));
    }
}
