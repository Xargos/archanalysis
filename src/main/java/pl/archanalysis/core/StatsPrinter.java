package pl.archanalysis.core;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.List;
import java.util.stream.Collectors;

public class StatsPrinter {
    static void print(DependencyRoot dependencyRoot) {
        List<DependencyNode> hotSpots = HotSpotFinder.find(dependencyRoot);
        RootAnalytics rootAnalytics = dependencyRoot.getRootAnalytics();
        System.out.println(String.format("Class No: %s", dependencyRoot.getDependencyNodes().size()));
        System.out.println(String.format("Hot spots no: %s", hotSpots.size()));
        System.out.println(String.format("Hot spots: %s", hotSpots.stream()
                .map(DependencyNode::getName)
                .collect(Collectors.toList())));
        print(rootAnalytics.getAllDepends(), "All depends");
        print(rootAnalytics.getDependsOn(), "Depends On");
        print(rootAnalytics.getDependsUpon(), "Depends Upon");
    }

    private static void print(DescriptiveStatistics allDepends, String type) {
        System.out.println(String.format("%s max: %s", type, allDepends.getMax()));
        System.out.println(String.format("%s mean: %s", type, allDepends.getMean()));
        System.out.println(String.format("%s variance: %s", type, allDepends.getVariance()));
        System.out.println(String.format("%s stddev: %s", type, allDepends.getStandardDeviation()));
    }
}
