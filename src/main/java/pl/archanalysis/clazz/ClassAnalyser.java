package pl.archanalysis.clazz;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaSource;
import pl.archanalysis.Dependency;
import pl.archanalysis.DependencyAnalysis;

import java.util.List;
import java.util.stream.Collectors;

public class ClassAnalyser {

    public static List<DependencyAnalysis> analyze(String codePath, JavaProjectBuilder builder) {
        return builder.getSources().stream()
                .map(javaSource -> createClassAnalysis(javaSource, codePath))
                .collect(Collectors.toList());
    }

    private static DependencyAnalysis createClassAnalysis(JavaSource javaSource, String codePath) {
        return new ClassAnalysis(
                javaSource.getClasses().get(0).getCanonicalName(),
                javaSource.getImports().stream()
                        .filter(s -> s.startsWith(codePath))
                        .map(name -> Dependency.builder()
                                .name(name)
                                .count(1)
                                .build())
                        .collect(Collectors.toList()));
    }
}
