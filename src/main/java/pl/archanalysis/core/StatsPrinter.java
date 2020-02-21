package pl.archanalysis.core;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import pl.archanalysis.core.analysis.DependencyAnalysisRoot;

public class StatsPrinter {
    static void print(DependencyAnalysisRoot dependencyAnalysisRoot) {
        System.out.println(String.format("Class No: %s", dependencyAnalysisRoot.getDependencyAnalysises().size()));
        print(dependencyAnalysisRoot.getAllDepends(), "All depends");
        print(dependencyAnalysisRoot.getDependsOn(), "Depends On");
        print(dependencyAnalysisRoot.getDependsUpon(), "Depends Upon");
    }

    private static void print(DescriptiveStatistics allDepends, String type) {
        System.out.println(String.format("%s max: %s", type, allDepends.getMax()));
        System.out.println(String.format("%s mean: %s", type, allDepends.getMean()));
        System.out.println(String.format("%s variance: %s", type, allDepends.getVariance()));
        System.out.println(String.format("%s stddev: %s", type, allDepends.getStandardDeviation()));
    }
}
