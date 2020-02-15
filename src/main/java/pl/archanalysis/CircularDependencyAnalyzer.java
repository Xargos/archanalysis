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
    private final Map<String, PackageAnalysis> packageAnalysisMap;

    private CircularDependencyAnalyzer(java.util.List<PackageAnalysis> packageAnalyses) {
        this.packages = packageAnalyses.stream()
                .map(PackageAnalysis::getPackageName)
                .collect(Collectors.toList());
        this.packageAnalysisMap = packageAnalyses.stream()
                .map(PackageAnalysis::copy)
                .collect(Collectors.toMap(PackageAnalysis::getPackageName, Function.identity()));
    }

    public static CircularDependencyAnalyzer newAnalyzer(java.util.List<PackageAnalysis> packageAnalyses) {
        return new CircularDependencyAnalyzer(packageAnalyses);
    }

    public java.util.List<PackageAnalysis> analyze() {
        for (int i = 0; i < this.packages.size(); i++) {
            String pack = packages.get(i);
            this.analyze(pack, HashSet.of(pack), io.vavr.collection.List.empty());
        }

        return new ArrayList<>(this.packageAnalysisMap.values());
    }

    private void analyze(String pack, Set<String> visitedPackages, List<PackageDependency> history) {
        PackageAnalysis packageAnalysis = packageAnalysisMap.get(pack);

        for (PackageDependency packageDependency : packageAnalysis.getPackageDependencies()) {
            String depName = packageDependency.getName();
            if (visitedPackages.contains(depName)) {
                this.setCircular(depName, history);
            } else {
                packages.remove(depName);
                this.analyze(depName, visitedPackages.add(depName), history.prepend(packageDependency));
            }
        }

    }

    private void setCircular(String depName, List<PackageDependency> deps) {
        deps.takeUntil(packageDependency -> packageDependency.getName().equalsIgnoreCase(depName))
                .forEach(dep -> dep.setCircular(true));
    }
}
