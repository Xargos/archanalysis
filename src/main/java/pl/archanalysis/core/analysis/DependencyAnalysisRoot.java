package pl.archanalysis.core.analysis;

import lombok.Value;

import java.util.List;

@Value
public class DependencyAnalysisRoot {
    private final RootAnalytics rootAnalytics;
    private final List<DependencyAnalysis> dependencyAnalysises;
}
