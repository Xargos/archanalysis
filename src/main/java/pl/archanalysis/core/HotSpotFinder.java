package pl.archanalysis.core;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import pl.archanalysis.core.analysis.DependencyAnalysis;
import pl.archanalysis.core.analysis.DependencyAnalysisRoot;
import pl.archanalysis.core.analysis.RootAnalytics;

import java.util.List;
import java.util.stream.Collectors;

public class HotSpotFinder {
    public static List<DependencyAnalysis> find(DependencyAnalysisRoot dependencyAnalysisRoot) {
        RootAnalytics rootAnalytics = dependencyAnalysisRoot.getRootAnalytics();
        double distance = calculateStdDevDistance(rootAnalytics.getAllDepends());
        return dependencyAnalysisRoot.getDependencyAnalysises().stream()
                .filter(dependencyAnalysis -> testHotSpot(distance, rootAnalytics, dependencyAnalysis))
                .collect(Collectors.toList());
    }

    public static boolean testHotSpot(double distance, RootAnalytics rootAnalytics, DependencyAnalysis dependencyAnalysis) {
        long value = rootAnalytics.getDependUponCount(dependencyAnalysis.getName())
                + dependencyAnalysis.getDependencies().size();
        return value > distance;
    }

    private static double calculateStdDevDistance(DescriptiveStatistics descriptiveStatistics) {
        double mean = descriptiveStatistics.getMean();
        double stddev = descriptiveStatistics.getStandardDeviation();
        return mean + (stddev * 2);
    }
}
