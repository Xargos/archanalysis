package pl.archanalysis.core;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.LinkSource;
import guru.nidi.graphviz.model.Node;
import pl.archanalysis.core.analysis.DependencyAnalysisRoot;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;

public class DependencyDrawer {

    public static void draw(DependencyAnalysisRoot dependencyAnalysisRoot,
                            String graphName) throws IOException {
        draw(dependencyAnalysisRoot, graphName, DependencyHotSpotMarker.dependSumHeatMap(dependencyAnalysisRoot));
    }

    public static void draw(DependencyAnalysisRoot dependencyAnalysisRoot,
                            String graphName,
                            DependencyHotSpotMarker heatMapDrawer) throws IOException {
        System.out.println("drawing");
        List<LinkSource> linkSources = dependencyAnalysisRoot.getDependencyAnalysises().stream()
                .map(packageAnalysis -> linkNodes(
                        packageAnalysis.getName(),
                        packageAnalysis.getDependencies(),
                        dependencyAnalysisRoot.getDependUponCount(packageAnalysis.getName()),
                        heatMapDrawer))
                .collect(Collectors.toList());

        Graph g = graph(graphName).directed()
                .graphAttr()
                .with(Rank.dir(LEFT_TO_RIGHT))
                .with(linkSources);
        Graphviz.fromGraph(g)
                .totalMemory(512_000_000)
                .render(Format.PNG)
                .toFile(new File("example/" + graphName + ".png"));
    }

    private static Node linkNodes(String name,
                                  List<Dependency> dependencies,
                                  Long dependsUpon,
                                  DependencyHotSpotMarker heatMapDrawer) {
        List<Link> links = dependencies.stream()
                .map(DependencyDrawer::buildLink)
                .collect(Collectors.toList());
        return node(name)
                .with(Label.of(name + " " + dependencies.size() + "/" + dependsUpon))
                .with(heatMapDrawer.heatMap(dependsUpon, dependencies.size()))
                .link(links);
    }

    private static Link buildLink(Dependency dep) {
        return Link.to(node(dep.getName()))
                .with(Label.of(dep.getCount().toString()))
                .with(dep.isCyclical() ? Color.RED : Color.BLACK);
    }
}
