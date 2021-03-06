package pl.archanalysis.analysers;

import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import pl.archanalysis.model.Dependency;
import pl.archanalysis.model.DependencyNode;
import pl.archanalysis.model.DependencyRoot;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StatefulCyclicalDependencyAnalyser {

    private final java.util.List<String> dependencies;
    private final Map<String, DependencyNode> dependencyAnalysisMap;

    private StatefulCyclicalDependencyAnalyser(java.util.Map<String, DependencyNode> dependencyNodes) {
        this.dependencies = new ArrayList<>(dependencyNodes.keySet());
        this.dependencyAnalysisMap = dependencyNodes.values().stream()
                .map(DependencyNode::copy)
                .collect(Collectors.toMap(DependencyNode::getName, Function.identity()));
    }

    static StatefulCyclicalDependencyAnalyser newCyclicalAnalyzer(DependencyRoot dependencyRoot) {
        return new StatefulCyclicalDependencyAnalyser(dependencyRoot.getDependencyNodes());
    }

    java.util.Map<String, DependencyNode> analyze() {
        for (int i = 0; i < this.dependencies.size(); i++) {
            String pack = dependencies.get(i);
            this.analyze(pack, HashSet.of(pack), io.vavr.collection.List.empty());
        }

        return this.dependencyAnalysisMap;
    }

    private void analyze(String pack, Set<String> visitedPackages, List<Dependency> history) {
        DependencyNode dependencyNode = dependencyAnalysisMap.get(pack);

        for (Dependency dependency : dependencyNode.getDependencies()) {
            String depName = dependency.getName();
            if (visitedPackages.contains(depName)) {
                this.setCyclical(depName, history);
            } else {
                if (dependencies.contains(depName)) {
                    dependencies.remove(depName);
                    this.analyze(depName, visitedPackages.add(depName), history.prepend(dependency));
                }
            }
        }
    }

    private void setCyclical(String depName, List<Dependency> deps) {
        deps.takeUntil(dependency -> dependency.getName().equalsIgnoreCase(depName))
                .forEach(dep -> dep.setCyclical(true));
    }
}
