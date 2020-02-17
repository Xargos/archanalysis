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
public class ClassAnalysis implements DependencyAnalysis {
    private final String name;
    private final List<Dependency> classDependencies;

    @Override
    public DependencyAnalysis copy() {
        return this.toBuilder()
                .classDependencies(this.classDependencies.stream()
                        .map(packageDependency -> packageDependency.toBuilder().build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Dependency> getDependencies() {
        return classDependencies;
    }

    @Override
    public DependencyAnalysis copyWithPackageDependencies(List<Dependency> dependencies) {
        return this.toBuilder().classDependencies(dependencies).build();
    }
}
