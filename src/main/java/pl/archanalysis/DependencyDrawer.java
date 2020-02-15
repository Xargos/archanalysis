package pl.archanalysis;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
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
import java.util.stream.Collectors;

import static guru.nidi.graphviz.attribute.Rank.RankDir.TOP_TO_BOTTOM;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;

public class DependencyDrawer {

    static void draw(List<PackageAnalysis> packageAnalyses) throws IOException {
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
                .map(DependencyDrawer::buildLink)
                .collect(Collectors.toList());
        return node(packageName).link(links);
    }

    private static Link buildLink(PackageDependency dep) {
        return Link.to(node(dep.getName()))
                .with(Label.of(dep.getCount().toString()))
                .with(dep.isCircular() ? Color.RED : Color.BLACK);
    }
}