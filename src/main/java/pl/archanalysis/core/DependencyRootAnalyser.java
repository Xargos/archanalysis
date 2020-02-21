package pl.archanalysis.core;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import pl.archanalysis.core.analysis.DependencyAnalysis;
import pl.archanalysis.core.analysis.DependencyAnalysisRoot;
import pl.archanalysis.core.analysis.RootAnalytics;

import java.util.List;
import java.util.Map;
import java.util.stream.DoubleStream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingLong;

public class DependencyRootAnalyser {

    public static DependencyAnalysisRoot analyzeRoot(List<DependencyAnalysis> dependencyAnalyses) {
        Map<String, Long> dependUponCount = dependencyAnalyses.stream()
                .flatMap(dependencyAnalysis -> dependencyAnalysis.getDependencies().stream())
                .collect(groupingBy(Dependency::getName, summingLong(Dependency::getCount)));

        double[] dependsUpon = dependUponCount.values().stream()
                .mapToDouble(Long::doubleValue)
                .toArray();

        double[] dependsOn = dependencyAnalyses.stream()
                .mapToDouble(deps -> deps.getDependencies().stream().mapToDouble(Dependency::getCount).sum())
                .toArray();

        double[] allDepends = DoubleStream.concat(DoubleStream.of(dependsUpon), DoubleStream.of(dependsOn))
                .toArray();

        return new DependencyAnalysisRoot(
                new RootAnalytics(
                        new DescriptiveStatistics(dependsOn),
                        new DescriptiveStatistics(dependsUpon),
                        new DescriptiveStatistics(allDepends),
                        dependUponCount),
                dependencyAnalyses
        );
    }
}
