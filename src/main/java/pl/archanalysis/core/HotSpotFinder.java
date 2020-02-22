package pl.archanalysis.core;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import pl.archanalysis.model.DependencyNode;
import pl.archanalysis.model.DependencyRoot;
import pl.archanalysis.model.RootAnalytics;

import java.util.List;
import java.util.stream.Collectors;

public class HotSpotFinder {
    public static List<DependencyNode> find(DependencyRoot dependencyRoot) {
        RootAnalytics rootAnalytics = dependencyRoot.getRootAnalytics();
        double distance = calculateStdDevDistance(rootAnalytics.getAllDepends());
        return dependencyRoot.getDependencyNodes().stream()
                .filter(dependencyAnalysis -> testHotSpot(distance, rootAnalytics, dependencyAnalysis))
                .collect(Collectors.toList());
    }

    public static boolean testHotSpot(double distance, RootAnalytics rootAnalytics, DependencyNode dependencyNode) {
        long value = rootAnalytics.getDependUponCount(dependencyNode.getName())
                + dependencyNode.getDependencies().size();
        return value > distance;
    }

    private static double calculateStdDevDistance(DescriptiveStatistics descriptiveStatistics) {
        double mean = descriptiveStatistics.getMean();
        double stddev = descriptiveStatistics.getStandardDeviation();
        return mean + (stddev * 2);
    }
}
