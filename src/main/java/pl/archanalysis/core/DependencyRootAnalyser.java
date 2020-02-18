package pl.archanalysis.core;

import pl.archanalysis.core.analysis.DependencyAnalysis;
import pl.archanalysis.core.analysis.DependencyAnalysisRoot;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingLong;

public class DependencyRootAnalyser {

    public static DependencyAnalysisRoot analyzeRoot(List<DependencyAnalysis> dependencyAnalyses) {
        Map<String, Long> dependUponCount = dependencyAnalyses.stream()
                .flatMap(dependencyAnalysis -> dependencyAnalysis.getDependencies().stream())
                .collect(groupingBy(Dependency::getName, summingLong(Dependency::getCount)));

        long maxDependsUpon = dependUponCount.values().stream()
                .mapToLong(Long::longValue)
                .max()
                .getAsLong();

        int maxDependsOn = dependencyAnalyses.stream()
                .mapToInt(deps -> deps.getDependencies().stream().mapToInt(Dependency::getCount).sum())
                .max()
                .getAsInt();

        return new DependencyAnalysisRoot(
                maxDependsOn,
                dependUponCount,
                maxDependsUpon,
                dependencyAnalyses
        );
    }
}
