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
                                                            DependencyAnalysis packageAnalysis) {
        return map.put(packageAnalysis.getName(),
                map.get(packageAnalysis.getName())
                        .map(pA -> mergeDependencies(packageAnalysis, pA))
                        .getOrElse(() -> packageAnalysis));
    }

    private static DependencyAnalysis mergeDependencies(DependencyAnalysis dependencyAnalysis, DependencyAnalysis pA) {
        Map<String, Dependency> dependencyMap = pA.getDependencies().stream()
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
