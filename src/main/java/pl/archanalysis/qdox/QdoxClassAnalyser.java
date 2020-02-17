package pl.archanalysis.qdox;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaSource;
import lombok.RequiredArgsConstructor;
import pl.archanalysis.core.ClassAnalyser;
import pl.archanalysis.core.Dependency;
import pl.archanalysis.core.analysis.ClassAnalysis;
import pl.archanalysis.core.analysis.DependencyAnalysis;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class QdoxClassAnalyser implements ClassAnalyser {

    private final String sourcePath;
    private final String pathSeparator;

    @Override
    public List<DependencyAnalysis> analyze(String codePath) {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceTree(new File(sourcePath + codePath.replace(".", pathSeparator)));

        return builder.getSources().stream()
                .map(javaSource -> QdoxClassAnalyser.createClassAnalysis(javaSource, codePath))
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
