package pl.archanalysis.core.analysis;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.commons.lang3.ClassUtils;
import pl.archanalysis.core.Dependency;

import java.util.List;
import java.util.stream.Collectors;

@Value
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class DependencyAnalysis {
    private final String name;
    private final List<Dependency> dependencies;

    public DependencyAnalysis copy() {
        return this.toBuilder()
                .dependencies(this.dependencies.stream()
                        .map(packageDependency -> packageDependency.toBuilder().build())
                        .collect(Collectors.toList()))
                .build();
    }

    public String getName() {
        return name;
    }

    public String getPackageCanonicalName() {
        return ClassUtils.getPackageCanonicalName(name);
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public DependencyAnalysis copyWithPackageDependencies(List<Dependency> dependencies) {
        return this.toBuilder().dependencies(dependencies).build();
    }
}
