package pl.archanalysis.core;

import pl.archanalysis.analysers.Analyser;
import pl.archanalysis.model.DependencyRoot;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static pl.archanalysis.core.DependencyDrawer.draw;

public class ClassAnalysis {

    private final String rootPackage;
    private final DependencyBuilder dependencyBuilder;
    private final Set<String> ignoreClass;
    private final List<Analyser> analysers;

    public ClassAnalysis(String rootPackage,
                         DependencyBuilder dependencyBuilder,
                         Set<String> ignoreClass,
                         List<Analyser> analysers) {
        this.rootPackage = rootPackage;
        this.dependencyBuilder = dependencyBuilder;
        this.ignoreClass = ignoreClass;
        this.analysers = analysers;
    }

    public void drawClassDependencyGraph() throws IOException {
        DependencyRoot dependencyRoot = dependencyBuilder.analyze(rootPackage, ignoreClass);

        for (Analyser analyser : analysers) {
            dependencyRoot = analyser.analyze(dependencyRoot);
        }

        StatsPrinter.print(dependencyRoot);
        draw(dependencyRoot, "ClassDependency");
    }

}
