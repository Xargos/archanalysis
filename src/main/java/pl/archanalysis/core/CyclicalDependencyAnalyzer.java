package pl.archanalysis.core;

import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import pl.archanalysis.core.analysis.DependencyAnalysis;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CyclicalDependencyAnalyzer {

    private final java.util.List<String> dependencies;
    private final Map<String, DependencyAnalysis> dependencyAnalysisMap;

    private CyclicalDependencyAnalyzer(java.util.List<DependencyAnalysis> dependencyAnalyses) {
        this.dependencies = dependencyAnalyses.stream()
                .map(DependencyAnalysis::getName)
                .collect(Collectors.toList());
        this.dependencyAnalysisMap = dependencyAnalyses.stream()
                .map(DependencyAnalysis::copy)
                .collect(Collectors.toMap(DependencyAnalysis::getName, Function.identity()));
    }

    public static CyclicalDependencyAnalyzer newCyclicalAnalyzer(java.util.List<DependencyAnalysis> packageAnalyses) {
        return new CyclicalDependencyAnalyzer(packageAnalyses);
    }

    public java.util.List<DependencyAnalysis> analyze() {
        for (int i = 0; i < this.dependencies.size(); i++) {
            String pack = dependencies.get(i);
            this.analyze(pack, HashSet.of(pack), io.vavr.collection.List.empty());
        }

        return new ArrayList<>(this.dependencyAnalysisMap.values());
    }

    private void analyze(String pack, Set<String> visitedPackages, List<Dependency> history) {
        DependencyAnalysis dependencyAnalysis = dependencyAnalysisMap.get(pack);

        for (Dependency dependency : dependencyAnalysis.getDependencies()) {
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
