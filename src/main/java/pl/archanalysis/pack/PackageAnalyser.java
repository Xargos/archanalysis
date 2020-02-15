package pl.archanalysis.pack;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaSource;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import org.apache.commons.lang3.ClassUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PackageAnalyser {

    public static List<PackageAnalysis> analyze(String codePackage, int level, JavaProjectBuilder builder) {
        return builder.getSources().stream()
                .map(javaSource -> createPackageAnalysis(javaSource, codePackage, level))
                .reduce(HashMap.empty(), PackageAnalysis::merge, HashMap::merge)
                .map(Tuple2::_2)
                .collect(Collectors.toList());
    }

    private static PackageAnalysis createPackageAnalysis(JavaSource javaSource, String codePath, int level) {
        return new PackageAnalysis(
                levelNode(level, javaSource.getPackageName()),
                javaSource.getImports().stream()
                        .filter(s -> s.startsWith(codePath))
                        .map(ClassUtils::getPackageCanonicalName)
                        .map(s -> levelNode(level, s))
                        .reduce(HashMap.empty(), PackageAnalyser::merge, HashMap::merge)
                        .map(params -> PackageDependency.builder()
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
