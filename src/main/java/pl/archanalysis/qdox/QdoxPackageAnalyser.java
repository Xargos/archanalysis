package pl.archanalysis.qdox;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaSource;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ClassUtils;
import pl.archanalysis.core.Dependency;
import pl.archanalysis.core.DependencyUtils;
import pl.archanalysis.core.PackageAnalyser;
import pl.archanalysis.core.analysis.DependencyAnalysis;
import pl.archanalysis.core.analysis.PackageAnalysis;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class QdoxPackageAnalyser implements PackageAnalyser {

    private final String sourcePath;
    private final String pathSeparator;

    @Override
    public List<DependencyAnalysis> analyze(String rootPackage) {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceTree(new File(sourcePath + rootPackage.replace(".", pathSeparator)));
        int level = rootPackage.split("\\.").length + 1;
        return builder.getSources().stream()
                .map(javaSource -> createPackageAnalysis(javaSource, rootPackage, level))
                .reduce(HashMap.empty(), DependencyUtils::merge, HashMap::merge)
                .map(Tuple2::_2)
                .collect(Collectors.toList());
    }

    private static DependencyAnalysis createPackageAnalysis(JavaSource javaSource, String codePath, int level) {
        return new PackageAnalysis(
                levelNode(level, javaSource.getPackageName()),
                javaSource.getImports().stream()
                        .filter(s -> s.startsWith(codePath))
                        .map(ClassUtils::getPackageCanonicalName)
                        .map(s -> levelNode(level, s))
                        .reduce(HashMap.empty(), QdoxPackageAnalyser::merge, HashMap::merge)
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

    private static HashMap<String, Integer> merge(HashMap<String, Integer> map, String name) {
        return map.put(name,
                map.get(name)
                        .map(count -> count + 1)
                        .getOrElse(() -> 1));
    }
}
