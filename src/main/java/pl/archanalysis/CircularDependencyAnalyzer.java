package pl.archanalysis;

import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CircularDependencyAnalyzer {

    private final java.util.List<String> packages;
    private final Map<String, DependencyAnalysis> packageAnalysisMap;

    private CircularDependencyAnalyzer(java.util.List<DependencyAnalysis> packageAnalyses) {
        this.packages = packageAnalyses.stream()
                .map(DependencyAnalysis::getName)
                .collect(Collectors.toList());
        this.packageAnalysisMap = packageAnalyses.stream()
                .map(DependencyAnalysis::copy)
                .collect(Collectors.toMap(DependencyAnalysis::getName, Function.identity()));
    }

    public static CircularDependencyAnalyzer newCircularAnalyzer(java.util.List<DependencyAnalysis> packageAnalyses) {
        return new CircularDependencyAnalyzer(packageAnalyses);
    }

    public java.util.List<DependencyAnalysis> analyze() {
        for (int i = 0; i < this.packages.size(); i++) {
            String pack = packages.get(i);
            this.analyze(pack, HashSet.of(pack), io.vavr.collection.List.empty());
        }

        return new ArrayList<>(this.packageAnalysisMap.values());
    }

    private void analyze(String pack, Set<String> visitedPackages, List<Dependency> history) {
        DependencyAnalysis dependencyAnalysis = packageAnalysisMap.get(pack);

        for (Dependency dependency : dependencyAnalysis.getDependencies()) {
            String depName = dependency.getName();
            if (visitedPackages.contains(depName)) {
                this.setCircular(depName, history);
            } else {
                packages.remove(depName);
                this.analyze(depName, visitedPackages.add(depName), history.prepend(dependency));
            }
        }

    }

    private void setCircular(String depName, List<Dependency> deps) {
        deps.takeUntil(dependency -> dependency.getName().equalsIgnoreCase(depName))
                .forEach(dep -> dep.setCircular(true));
    }
}
