package pl.archanalysis.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.Map;
import java.util.Set;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor
public class RootAnalytics {
    private final DescriptiveStatistics dependsOn;
    private final DescriptiveStatistics dependsUpon;
    private final DescriptiveStatistics allDepends;
    private final Map<String, Long> dependUponCount;
    private final Map<String, Set<String>> dependencyUponMap;
    private final Set<String> roots;
    private final Map<String, Set<String>> nodesRoots;

    public Long getDependUponCount(String name) {
        return dependUponCount.getOrDefault(name, 0L);
    }

    public boolean isRoot(String name) {
        return roots.contains(name);
    }

    public Set<String> getDependUpon(String name) {
        return dependencyUponMap.getOrDefault(name, Set.of());
    }
}