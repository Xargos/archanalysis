package pl.archanalysis;

import lombok.Value;

import java.util.Set;

@Value
public class PackageAnalysis {
    private final String packageName;
    private final Set<String> packageDependencies;
}
