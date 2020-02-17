package pl.archanalysis.core.analysis;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import pl.archanalysis.core.Dependency;

import java.util.List;
import java.util.stream.Collectors;

@Value
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class PackageAnalysis implements DependencyAnalysis {
    private final String packageName;
    private final List<Dependency> packageDependencies;

    @Override
    public DependencyAnalysis copy() {
        return this.toBuilder()
                .packageDependencies(this.packageDependencies.stream()
                        .map(packageDependency -> packageDependency.toBuilder().build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public String getName() {
        return packageName;
    }

    @Override
    public List<Dependency> getDependencies() {
        return packageDependencies;
    }

    @Override
    public DependencyAnalysis copyWithPackageDependencies(List<Dependency> dependencies) {
        return this.toBuilder().packageDependencies(dependencies).build();
    }
}
