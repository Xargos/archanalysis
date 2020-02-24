package pl.archanalysis.analysers;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import pl.archanalysis.model.Dependency;
import pl.archanalysis.model.DependencyNode;
import pl.archanalysis.model.DependencyRoot;
import pl.archanalysis.model.RootAnalytics;

import java.util.*;
import java.util.stream.DoubleStream;

import static java.util.stream.Collectors.*;

public class DependencyRootAnalyser implements Analyser {

    @Override
    public DependencyRoot analyze(DependencyRoot dependencyRoot) {
        List<DependencyNode> dependencyNodes = new ArrayList<>(dependencyRoot.getDependencyNodes().values());

        Map<String, Set<String>> dependencyUponMap = buildDependencyUponMap(dependencyNodes);

        Map<String, Long> dependUponCount = buildDependUponCount(dependencyNodes);

        Set<String> roots = findRoots(dependencyNodes, dependUponCount);

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
                                dependUponCount,
                                dependencyUponMap,
                                roots,
                                Collections.emptyMap()))
                .build();
    }

    private Set<String> findRoots(List<DependencyNode> dependencyNodes, Map<String, Long> dependencyUponMap) {
        return dependencyNodes.stream()
                .filter(deps -> dependencyUponMap.getOrDefault(deps.getName(), 0L) == 0)
                .map(DependencyNode::getName)
                .collect(toSet());
    }

    private Map<String, Long> buildDependUponCount(List<DependencyNode> dependencyNodes) {
        return dependencyNodes.stream()
                .flatMap(dependencyAnalysis -> dependencyAnalysis.getDependencies().stream())
                .collect(groupingBy(Dependency::getName, summingLong(Dependency::getCount)));
    }

    private Map<String, Set<String>> buildDependencyUponMap(List<DependencyNode> dependencyNodes) {
        return dependencyNodes.stream()
                .flatMap(dependencyAnalysis -> dependencyAnalysis.getDependencies().stream()
                        .map(Dependency::getName)
                        .map(dependencyName -> Tuple.of(dependencyName, dependencyAnalysis.getName())))
                .collect(groupingBy(Tuple2::_1, mapping(Tuple2::_2, toSet())));
    }
}
