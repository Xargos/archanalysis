package pl.archanalysis.analysers;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import pl.archanalysis.model.DependencyNode;
import pl.archanalysis.model.DependencyRoot;
import pl.archanalysis.model.RootAnalytics;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

class ModuleMarkerAnalyser implements Analyser {

    @Override
    public DependencyRoot analyze(DependencyRoot dependencyRoot) {
        RootAnalytics rootAnalytics = dependencyRoot.getRootAnalytics();
        Set<Tuple2<String, String>> collect = rootAnalytics.getRoots().stream()
                .flatMap(root -> markBranchRoots(
                        root,
                        dependencyRoot.getDependencyNode(root),
                        dependencyRoot,
                        io.vavr.collection.HashSet.empty()))
                .collect(toSet());

        Map<String, Set<String>> nodesRoots = rootAnalytics.getRoots().stream()
                .flatMap(root -> markBranchRoots(
                        dependencyRoot.getDependencyNode(root).getPackageCanonicalName(),
                        dependencyRoot.getDependencyNode(root),
                        dependencyRoot,
                        io.vavr.collection.HashSet.empty()))
                .distinct()
                .collect(groupingBy(Tuple2::_1, mapping(Tuple2::_2, toSet())));

        return dependencyRoot.toBuilder()
                .rootAnalytics(dependencyRoot.getRootAnalytics().toBuilder()
                        .nodesRoots(nodesRoots)
                        .build())
                .build();
    }

    private Stream<Tuple2<String, String>> markBranchRoots(String root,
                                                           DependencyNode dependencyNode,
                                                           DependencyRoot dependencyRoot,
                                                           io.vavr.collection.Set<String> visitedNodes) {
        if (visitedNodes.contains(dependencyNode.getName())) {
            return Stream.empty();
        }
        io.vavr.collection.Set<String> newVisitedNodes = visitedNodes.add(dependencyNode.getName());
        return Stream.concat(
                Stream.of(Tuple.of(dependencyNode.getName(), root)),
                dependencyNode.getDependencies().stream()
                        .map(dep -> dependencyRoot.getDependencyNode(dep.getName()))
                        .flatMap(dn -> markBranchRoots(root, dn, dependencyRoot, newVisitedNodes)));
    }
}
