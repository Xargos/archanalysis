package pl.archanalysis.core.analysis;

import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public class DependencyAnalysisRoot {
    private final Integer maxDependsOn;

    private final Map<String, Long> dependUponCount;

    private final Long maxDependsUpon;

    private final List<DependencyAnalysis> dependencyAnalysises;

    public long getMaxDependsSum() {
        return maxDependsOn + maxDependsUpon;
    }

    public Long getDependUponCount(String name) {
        return dependUponCount.getOrDefault(name, 0L);
    }
}
