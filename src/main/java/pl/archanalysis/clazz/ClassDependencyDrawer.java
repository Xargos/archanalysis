package pl.archanalysis.clazz;

import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.LinkSource;
import guru.nidi.graphviz.model.Node;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static guru.nidi.graphviz.attribute.Rank.RankDir.TOP_TO_BOTTOM;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;

public class ClassDependencyDrawer {

    public static void draw(List<ClassAnalysis> classAnalyses) throws IOException {
        List<LinkSource> linkSources = classAnalyses.stream()
                .map(classAnalysis -> linkNodes(
                        classAnalysis.getJavaSource().getClasses().get(0).getCanonicalName(),
                        classAnalysis.getPackageDependencies()))
                .collect(Collectors.toList());

        Graph g = graph("ClassDependency").directed()
                .graphAttr()
                .with(Rank.dir(TOP_TO_BOTTOM))
                .with(linkSources);
        Graphviz.fromGraph(g).render(Format.PNG).toFile(new File("example/ClassDependency.png"));
    }

    private static Node linkNodes(String packageName, Set<String> packageDependencies) {
        List<Link> links = packageDependencies.stream()
                .map(ClassDependencyDrawer::buildLink)
                .collect(Collectors.toList());
        return node(packageName).link(links);
    }

    private static Link buildLink(String dep) {
        return Link.to(node(dep));
    }
}
