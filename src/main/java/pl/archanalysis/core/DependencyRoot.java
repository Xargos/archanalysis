package pl.archanalysis.core;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder(toBuilder = true)
public class DependencyRoot {
    private final RootAnalytics rootAnalytics;
    private final List<DependencyNode> dependencyNodes;
}
