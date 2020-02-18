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
import pl.archanalysis.core.analysis.DependencyAnalysis;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static guru.nidi.graphviz.attribute.Rank.RankDir.TOP_TO_BOTTOM;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class DependencyDrawer {

    public static void draw(List<DependencyAnalysis> packageAnalyses,
                            String graphName) throws IOException {

        Map<String, Long> dependUponCount = packageAnalyses.stream()
                .flatMap(dependencyAnalysis -> dependencyAnalysis.getDependencies().stream())
                .map(Dependency::getName)
                .collect(groupingBy(Function.identity(), counting()));

        long maxDependsUpon = dependUponCount.values().stream()
                .mapToLong(Long::longValue)
                .max()
                .getAsLong();

        List<LinkSource> linkSources = packageAnalyses.stream()
                .map(packageAnalysis -> linkNodes(
                        packageAnalysis.getName(),
                        packageAnalysis.getDependencies(),
                        dependUponCount,
                        maxDependsUpon))
                .collect(Collectors.toList());

        Graph g = graph(graphName).directed()
                .graphAttr()
                .with(Rank.dir(TOP_TO_BOTTOM))
                .with(linkSources);
        Graphviz.fromGraph(g).render(Format.PNG).toFile(new File("example/" + graphName + ".png"));
    }

    private static Node linkNodes(String name,
                                  List<Dependency> dependencies,
                                  Map<String, Long> dependUponCount,
                                  long maxDependsUpon) {
        List<Link> links = dependencies.stream()
                .map(DependencyDrawer::buildLink)
                .collect(Collectors.toList());
        Long dependsUpon = dependUponCount.getOrDefault(name, 0L);
        return node(name)
                .with(Label.of(name + " " + dependencies.size() + "/" + dependsUpon))
                .with(heatMap(maxDependsUpon, dependsUpon))
                .link(links);
    }

    private static Color heatMap(long maxDependsUpon, Long dependsUpon) {
        int heat = (int) ((dependsUpon * 511D) / maxDependsUpon);
        return Color.rgb(heat > 255 ? heat - 256 : 0, heat < 256 ? 255 - heat : 0, 0);
    }

    private static Link buildLink(Dependency dep) {
        return Link.to(node(dep.getName()))
                .with(Label.of(dep.getCount().toString()))
                .with(dep.isCircular() ? Color.RED : Color.BLACK);
    }
}
