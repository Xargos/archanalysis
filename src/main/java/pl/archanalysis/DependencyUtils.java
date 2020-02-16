package pl.archanalysis;

import io.vavr.collection.HashMap;
import lombok.Builder;
import lombok.Value;
import pl.archanalysis.pack.PackageAnalysis;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Value
@Builder(toBuilder = true)
public class DependencyUtils {

    public static HashMap<String, DependencyAnalysis> merge(HashMap<String, DependencyAnalysis> map,
                                                            DependencyAnalysis dependencyAnalysis) {
        return map.put(dependencyAnalysis.getName(),
                map.get(dependencyAnalysis.getName())
                        .map(pA -> mergeDependencies(dependencyAnalysis, pA))
                        .getOrElse(() -> dependencyAnalysis));
    }

    private static DependencyAnalysis mergeDependencies(DependencyAnalysis dependencyAnalysis, DependencyAnalysis dA) {
        Map<String, Dependency> dependencyMap = dA.getDependencies().stream()
                .collect(Collectors.toMap(Dependency::getName, Function.identity()));
        dependencyAnalysis.getDependencies()
                .forEach(packageDependency -> dependencyMap.compute(packageDependency.getName(),
                        (s, packageDependency1) -> merge(packageDependency1, packageDependency)));
        return dependencyAnalysis.copyWithPackageDependencies(new ArrayList<>(dependencyMap.values()));
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
