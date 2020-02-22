package pl.archanalysis.core.model;

import io.vavr.collection.HashMap;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.commons.lang3.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Value
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class DependencyNode {
    private final String name;
    private final List<Dependency> dependencies;

    public DependencyNode copy() {
        return this.toBuilder()
                .dependencies(this.dependencies.stream()
                        .map(dependency -> dependency.toBuilder().build())
                        .collect(Collectors.toList()))
                .build();
    }

    public String getPackageCanonicalName() {
        return ClassUtils.getPackageCanonicalName(name);
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public DependencyNode copyWithPackageDependencies(List<Dependency> dependencies) {
        return this.toBuilder().dependencies(dependencies).build();
    }

    public static HashMap<String, DependencyNode> mergeDependencies(HashMap<String, DependencyNode> map,
                                                                    DependencyNode dependencyNode) {
        return map.put(dependencyNode.getName(),
                map.get(dependencyNode.getName())
                        .map(pA -> mergeDependencies(dependencyNode, pA))
                        .getOrElse(() -> dependencyNode));
    }

    private static DependencyNode mergeDependencies(DependencyNode dependencyNode, DependencyNode dA) {
        Map<String, Dependency> dependencyMap = dA.getDependencies().stream()
                .collect(Collectors.toMap(Dependency::getName, Function.identity()));
        dependencyNode.getDependencies()
                .forEach(packageDependency -> dependencyMap.compute(packageDependency.getName(),
                        (s, packageDependency1) -> merge(packageDependency1, packageDependency)));
        return dependencyNode.copyWithPackageDependencies(new ArrayList<>(dependencyMap.values()));
    }

    private static Dependency merge(Dependency original, Dependency dependency) {
        if (original == null) {
            return dependency;
        }
        return original
                .toBuilder()
                .count(original.getCount() + dependency.getCount())
                .build();
    }
}
