package pl.archanalysis.model;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder(toBuilder = true)
public class DependencyRoot {
    private final RootAnalytics rootAnalytics;
    private final Map<String, DependencyNode> dependencyNodes;

    public DependencyNode getDependencyNode(String name) {
        return dependencyNodes.get(name);
    }
}
