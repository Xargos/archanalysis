package pl.archanalysis.core.model;

import lombok.Value;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.Map;

@Value
public class RootAnalytics {
    private final DescriptiveStatistics dependsOn;
    private final DescriptiveStatistics dependsUpon;
    private final DescriptiveStatistics allDepends;
    private final Map<String, Long> dependUponCount;

    public Long getDependUponCount(String name) {
        return dependUponCount.getOrDefault(name, 0L);
    }
}