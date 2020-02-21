package pl.archanalysis.core.analysers;

import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ClassUtils;
import pl.archanalysis.core.Analyser;
import pl.archanalysis.core.Dependency;
import pl.archanalysis.core.DependencyRoot;
import pl.archanalysis.core.DependencyNode;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class PackageAnalyser implements Analyser {

    private final String rootPackage;

    @Override
    public DependencyRoot analyze(DependencyRoot dependencyRoot) {
        return dependencyRoot.toBuilder()
                .dependencyNodes(this.analyze(dependencyRoot.getDependencyNodes()))
                .build();
    }

    private List<DependencyNode> analyze(List<DependencyNode> dependencyAnalyses) {
        int level = rootPackage.split("\\.").length + 1;
        return dependencyAnalyses.stream()
                .filter(dependencyAnalysis -> dependencyAnalysis.getPackageCanonicalName().split("\\.").length >= level)
                .map(dependencyAnalysis -> createPackageAnalysis(dependencyAnalysis, level))
                .reduce(HashMap.empty(), DependencyNode::mergeDependencies, HashMap::merge)
                .map(Tuple2::_2)
                .collect(Collectors.toList());
    }

    private DependencyNode createPackageAnalysis(DependencyNode dependencyNode, int level) {
        return new DependencyNode(
                levelNode(level, dependencyNode.getPackageCanonicalName()),
                dependencyNode.getDependencies().stream()
                        .map(Dependency::getName)
                        .map(ClassUtils::getPackageCanonicalName)
                        .filter(dep -> dep.split("\\.").length >= level)
                        .map(dep -> levelNode(level, dep))
                        .reduce(HashMap.empty(), this::mergeRaw, HashMap::merge)
                        .map(params -> Dependency.builder()
                                .name(params._1())
                                .count(params._2())
                                .build())
                        .collect(Collectors.toList()));
    }

    private HashMap<String, Integer> mergeRaw(HashMap<String, Integer> map, String name) {
        return map.put(name,
                map.get(name)
                        .map(count -> count + 1)
                        .getOrElse(() -> 1));
    }

    private String levelNode(int level, String dep) {
        String[] split = dep.split("\\.");
        return Stream.of(split)
                .limit(level)
                .collect(Collectors.joining("."));
    }
}
