package pl.archanalysis.qdox;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaSource;
import lombok.RequiredArgsConstructor;
import pl.archanalysis.core.DependencyBuilder;
import pl.archanalysis.core.model.Dependency;
import pl.archanalysis.core.model.DependencyNode;
import pl.archanalysis.core.model.DependencyRoot;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class QdoxDependencyBuilder implements DependencyBuilder {

    private final String sourcePath;
    private final String pathSeparator;

    @Override
    public DependencyRoot analyze(String codePath) {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceTree(new File(sourcePath + codePath.replace(".", pathSeparator)));

        List<DependencyNode> dependencyNodes = builder.getSources().stream()
                .map(javaSource -> QdoxDependencyBuilder.createClassAnalysis(javaSource, codePath))
                .collect(Collectors.toList());

        return DependencyRoot.builder()
                .dependencyNodes(dependencyNodes)
                .build();
    }

    private static DependencyNode createClassAnalysis(JavaSource javaSource, String codePath) {
        return new DependencyNode(
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
