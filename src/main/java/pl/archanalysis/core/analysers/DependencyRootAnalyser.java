package pl.archanalysis.core.analysers;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import pl.archanalysis.core.model.Dependency;
import pl.archanalysis.core.model.DependencyNode;
import pl.archanalysis.core.model.DependencyRoot;
import pl.archanalysis.core.model.RootAnalytics;

import java.util.List;
import java.util.Map;
import java.util.stream.DoubleStream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingLong;

public class DependencyRootAnalyser implements Analyser {

    @Override
    public DependencyRoot analyze(DependencyRoot dependencyRoot) {
        List<DependencyNode> dependencyNodes = dependencyRoot.getDependencyNodes();
        Map<String, Long> dependUponCount = dependencyNodes.stream()
                .flatMap(dependencyAnalysis -> dependencyAnalysis.getDependencies().stream())
                .collect(groupingBy(Dependency::getName, summingLong(Dependency::getCount)));

        double[] dependsUpon = dependUponCount.values().stream()
                .mapToDouble(Long::doubleValue)
                .toArray();

        double[] dependsOn = dependencyNodes.stream()
                .mapToDouble(deps -> deps.getDependencies().stream().mapToDouble(Dependency::getCount).sum())
                .toArray();

        double[] allDepends = DoubleStream.concat(DoubleStream.of(dependsUpon), DoubleStream.of(dependsOn))
                .toArray();

        return dependencyRoot.toBuilder()
                .rootAnalytics(
                        new RootAnalytics(
                                new DescriptiveStatistics(dependsOn),
                                new DescriptiveStatistics(dependsUpon),
                                new DescriptiveStatistics(allDepends),
                                dependUponCount))
                .build();
    }
}
