package pl.archanalysis.analysers;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import pl.archanalysis.model.DependencyNode;
import pl.archanalysis.model.DependencyRoot;
import pl.archanalysis.model.RootAnalytics;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ModuleMarkerAnalyser implements Analyser {

    @Override
    public DependencyRoot analyze(DependencyRoot dependencyRoot) {
        RootAnalytics rootAnalytics = dependencyRoot.getRootAnalytics();
        Set<Tuple2<String, String>> collect = rootAnalytics.getRoots().stream()
                .flatMap(root -> markBranchDeps(
                        dependencyRoot.getDependencyNode(root).getPackageCanonicalName(),
                        dependencyRoot.getDependencyNode(root),
                        dependencyRoot))
                .collect(Collectors.toSet());

        Map<String, String> predictedModule = rootAnalytics.getRoots().stream()
                .flatMap(root -> markBranchDeps(
                        dependencyRoot.getDependencyNode(root).getPackageCanonicalName(),
                        dependencyRoot.getDependencyNode(root),
                        dependencyRoot))
                .collect(Collectors.toMap(Tuple2::_1, Tuple2::_2));

        return dependencyRoot.toBuilder()
                .rootAnalytics(dependencyRoot.getRootAnalytics().toBuilder()
                        .predictedModule(predictedModule)
                        .build())
                .build();
    }

    private Stream<Tuple2<String, String>> markBranchDeps(String moduleName,
                                                          DependencyNode dependencyNode,
                                                          DependencyRoot dependencyRoot) {
        RootAnalytics rootAnalytics = dependencyRoot.getRootAnalytics();
        boolean isPartOfModule = isPartOfModule(moduleName, dependencyNode, rootAnalytics);
        if (isPartOfModule) {
            return Stream.concat(
                    Stream.of(Tuple.of(dependencyNode.getName(), moduleName)),
                    dependencyNode.getDependencies().stream()
                            .filter(dependency -> !dependency.getName().equalsIgnoreCase(dependencyNode.getName()))
                            .map(dep -> dependencyRoot.getDependencyNode(dep.getName()))
                            .flatMap(dn -> markBranchDeps(moduleName, dn, dependencyRoot)));
        }
        return Stream.empty();
    }

    private boolean isPartOfModule(String moduleName, DependencyNode dependencyNode, RootAnalytics rootAnalytics) {
        return rootAnalytics.getDependUpon(dependencyNode.getName())
                .stream()
                .allMatch(name -> name.startsWith(moduleName));
    }
}
