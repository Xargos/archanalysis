package pl.archanalysis;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CircularDependencyAnalyzer {

    private List<String> packages;
    private Map<String, PackageAnalysis> packageAnalysisMap;

    public List<PackageAnalysis> analyze(List<PackageAnalysis> packageAnalyses) {
        this.packages = packageAnalyses.stream()
                .map(PackageAnalysis::getPackageName)
                .collect(Collectors.toList());
        this.packageAnalysisMap = packageAnalyses.stream()
                .map(PackageAnalysis::copy)
                .collect(Collectors.toMap(PackageAnalysis::getPackageName, Function.identity()));

        for (int i = 0; i < this.packages.size(); i++) {
            String pack = packages.get(i);
            this.analyze(pack, HashSet.of(pack), HashSet.empty());
        }

        return new ArrayList<>(this.packageAnalysisMap.values());
    }

    private void analyze(String pack, Set<String> visitedPackages, Set<PackageDependency> history) {
        PackageAnalysis packageAnalysis = packageAnalysisMap.get(pack);

        for (PackageDependency packageDependency : packageAnalysis.getPackageDependencies()) {
            String depName = packageDependency.getName();
            if (visitedPackages.contains(depName)) {
                this.setCircular(history.add(packageDependency));
            } else {
                packages.remove(depName);
                this.analyze(depName, visitedPackages.add(depName), history.add(packageDependency));
            }
        }

    }

    private void setCircular(Set<PackageDependency> deps) {
        deps.forEach(dep -> dep.setCircular(true));
    }
}
