package pl.archanalysis.qdox;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaSource;
import lombok.RequiredArgsConstructor;
import pl.archanalysis.core.DependencyBuilder;
import pl.archanalysis.model.Dependency;
import pl.archanalysis.model.DependencyNode;
import pl.archanalysis.model.DependencyRoot;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class QdoxDependencyBuilder implements DependencyBuilder {

    private final String sourcePath;
    private final String pathSeparator;

    @Override
    public DependencyRoot analyze(String codePath) {
        return analyze(codePath, Collections.emptySet());
    }

    @Override
    public DependencyRoot analyze(String codePath, Set<String> ignoreClass) {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceTree(new File(sourcePath + codePath.replace(".", pathSeparator)));

        Map<String, DependencyNode> dependencyNodes = builder.getSources().stream()
                .map(javaSource -> QdoxDependencyBuilder.createClassAnalysis(javaSource, codePath))
                .filter(dependencyAnalysis -> !ignoreClass.contains(dependencyAnalysis.getName()))
                .collect(Collectors.toMap(DependencyNode::getName, Function.identity()));

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
