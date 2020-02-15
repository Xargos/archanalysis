package pl.archanalysis.clazz;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaSource;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import pl.archanalysis.pack.PackageAnalysis;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClassAnalyser {

    public static List<ClassAnalysis> analyze(String codePath, JavaProjectBuilder builder) {
        return builder.getSources().stream()
                .map(javaSource -> createClassAnalysis(javaSource, codePath))
                .collect(Collectors.toList());
    }

    private static ClassAnalysis createClassAnalysis(JavaSource javaSource, String codePath) {
        return new ClassAnalysis(
                javaSource,
                javaSource.getImports().stream()
                        .filter(s -> s.startsWith(codePath))
                        .collect(Collectors.toSet()));
    }
}
