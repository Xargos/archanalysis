package pl.archanalysis.core;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.LinkSource;
import guru.nidi.graphviz.model.Node;
import pl.archanalysis.model.Dependency;
import pl.archanalysis.model.DependencyRoot;
import pl.archanalysis.model.RootAnalytics;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;
import static guru.nidi.graphviz.model.Factory.graph;
import static guru.nidi.graphviz.model.Factory.node;

public class DependencyDrawer {

    public static void draw(DependencyRoot dependencyRoot,
                            String graphName) throws IOException {
        draw(
                dependencyRoot,
                graphName,
                LabelDrawer.dependAllHotSpot(dependencyRoot.getRootAnalytics()),
                ModuleDrawer.newModuleDrawer(dependencyRoot));
    }

    public static void draw(DependencyRoot dependencyRoot,
                            String graphName,
                            LabelDrawer labelDrawer,
                            ModuleDrawer moduleDrawer) throws IOException {
        System.out.println("drawing");
        List<LinkSource> linkSources = dependencyRoot.getDependencyNodes().values().stream()
                .map(dependencyNode -> linkNodes(
                        dependencyNode.getName(),
                        dependencyNode.getDependencies(),
                        dependencyRoot.getRootAnalytics(),
                        labelDrawer,
                        moduleDrawer))
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
                                  RootAnalytics rootAnalytics,
                                  LabelDrawer labelDrawer,
                                  ModuleDrawer moduleDrawer) {
        List<Link> links = dependencies.stream()
                .map(DependencyDrawer::buildLink)
                .collect(Collectors.toList());
        return node(name)
                .with(markRoot(rootAnalytics.isRoot(name)).and(markLeaf(name, dependencies)))
                .with(labelDrawer.draw(name, rootAnalytics.getDependUponCount(name), dependencies.size()))
                .with(moduleDrawer.getColorForModule(name))
                .link(links);
    }

    private static Style markLeaf(String name, List<Dependency> dependencies) {
        return dependencies.stream().allMatch(dep -> dep.getName().equalsIgnoreCase(name)) ? Style.DASHED : Style.SOLID;
    }

    private static Style markRoot(boolean isRoot) {
        return isRoot ? Style.DIAGONALS : Style.SOLID;
    }

    private static Link buildLink(Dependency dep) {
        return Link.to(node(dep.getName()))
                .with(Label.of(dep.getCount().toString()))
                .with(dep.isCyclical() ? Color.RED : Color.BLACK);
    }
}
