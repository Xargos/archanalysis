package pl.archanalysis.core;

import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import org.apache.commons.lang3.ClassUtils;
import pl.archanalysis.core.analysis.DependencyAnalysis;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PackageAnalyser {

    public static List<DependencyAnalysis> analyze(String rootPackage, List<DependencyAnalysis> dependencyAnalyses) {
        int level = rootPackage.split("\\.").length + 1;
        return dependencyAnalyses.stream()
                .filter(dependencyAnalysis -> dependencyAnalysis.getPackageCanonicalName().split("\\.").length >= level)
                .map(dependencyAnalysis -> createPackageAnalysis(dependencyAnalysis, level))
                .reduce(HashMap.empty(), DependencyUtils::mergeDependencies, HashMap::merge)
                .map(Tuple2::_2)
                .collect(Collectors.toList());
    }

    private static DependencyAnalysis createPackageAnalysis(DependencyAnalysis dependencyAnalysis, int level) {
        return new DependencyAnalysis(
                levelNode(level, dependencyAnalysis.getPackageCanonicalName()),
                dependencyAnalysis.getDependencies().stream()
                        .map(Dependency::getName)
                        .map(ClassUtils::getPackageCanonicalName)
                        .filter(dep -> dep.split("\\.").length >= level)
                        .map(dep -> levelNode(level, dep))
                        .reduce(HashMap.empty(), DependencyUtils::mergeRaw, HashMap::merge)
                        .map(params -> Dependency.builder()
                                .name(params._1())
                                .count(params._2())
                                .build())
                        .collect(Collectors.toList()));
    }

    private static String levelNode(int level, String dep) {
        String[] split = dep.split("\\.");
        return Stream.of(split)
                .limit(level)
                .collect(Collectors.joining("."));
    }
}
