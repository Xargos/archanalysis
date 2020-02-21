package pl.archanalysis.core.analysis;

import lombok.Value;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.List;
import java.util.Map;

@Value
public class DependencyAnalysisRoot {
    private final DescriptiveStatistics dependsOn;
    private final DescriptiveStatistics dependsUpon;
    private final DescriptiveStatistics allDepends;
    private final Map<String, Long> dependUponCount;
    private final List<DependencyAnalysis> dependencyAnalysises;

    public Long getDependUponCount(String name) {
        return dependUponCount.getOrDefault(name, 0L);
    }
}
