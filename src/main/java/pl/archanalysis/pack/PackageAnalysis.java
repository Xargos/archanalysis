package pl.archanalysis.pack;

import io.vavr.collection.HashMap;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Value
@Builder(toBuilder = true)
public class PackageAnalysis {
    private final String packageName;
    private final List<PackageDependency> packageDependencies;

    public PackageAnalysis copy() {
        return this.toBuilder()
                .packageDependencies(this.packageDependencies.stream()
                        .map(packageDependency -> packageDependency.toBuilder().build())
                        .collect(Collectors.toList()))
                .build();
    }

    public static HashMap<String, PackageAnalysis> merge(HashMap<String, PackageAnalysis> map,
                                                         PackageAnalysis packageAnalysis) {
        return map.put(packageAnalysis.getPackageName(),
                map.get(packageAnalysis.getPackageName())
                        .map(pA -> mergeDependencies(packageAnalysis, pA))
                        .getOrElse(() -> packageAnalysis));
    }

    private static PackageAnalysis mergeDependencies(PackageAnalysis packageAnalysis, PackageAnalysis pA) {
        Map<String, PackageDependency> dependencyMap = pA.getPackageDependencies().stream()
                .collect(Collectors.toMap(PackageDependency::getName, Function.identity()));
        packageAnalysis.packageDependencies
                .forEach(packageDependency -> dependencyMap.compute(packageDependency.getName(), (s, packageDependency1) -> merge(packageDependency1, packageDependency)));
        return packageAnalysis.toBuilder().packageDependencies(new ArrayList<>(dependencyMap.values())).build();
    }

    private static PackageDependency merge(PackageDependency original, PackageDependency packageDependency) {
        if (original == null) {
            return packageDependency;
        }
        return original
                .toBuilder()
                .count(original.getCount() + packageDependency.getCount())
                .build();
    }
}
