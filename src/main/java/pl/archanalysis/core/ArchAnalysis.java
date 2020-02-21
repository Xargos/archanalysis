package pl.archanalysis.core;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaSource;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import pl.archanalysis.core.analysers.CyclicalDependencyAnalyser;
import pl.archanalysis.core.analysers.DependencyRootAnalyser;
import pl.archanalysis.core.analysers.PackageAnalyser;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static pl.archanalysis.core.DependencyDrawer.draw;

public class ArchAnalysis {

    private final String rootPackage;
    private final DependencyBuilder dependencyBuilder;
    private final Set<String> ignoreClass;
    private final PackageAnalyser packageAnalyser;
    private final DependencyRootAnalyser dependencyRootAnalyser = new DependencyRootAnalyser();
    private final CyclicalDependencyAnalyser cyclicalDependencyAnalyser = new CyclicalDependencyAnalyser();

    public ArchAnalysis(String rootPackage,
                        DependencyBuilder dependencyBuilder,
                        Set<String> ignoreClass) {
        this.rootPackage = rootPackage;
        this.dependencyBuilder = dependencyBuilder;
        this.ignoreClass = ignoreClass;
        this.packageAnalyser = new PackageAnalyser(rootPackage);
    }

    public void drawClassDependencyGraph() throws IOException {
        DependencyRoot dependencyRoot = dependencyBuilder.analyze(rootPackage);
        dependencyRoot = dependencyRoot.toBuilder().dependencyNodes(
                dependencyRoot.getDependencyNodes().stream()
                        .filter(dependencyAnalysis -> !ignoreClass.contains(dependencyAnalysis.getName()))
                        .collect(Collectors.toList())).build();
        dependencyRoot = cyclicalDependencyAnalyser.analyze(dependencyRoot);
        dependencyRoot = dependencyRootAnalyser.analyze(dependencyRoot);
        StatsPrinter.print(dependencyRoot);
        draw(dependencyRoot, "ClassDependency");
    }

    public void drawPackageDependencyGraph() throws IOException {
        DependencyRoot analyzedPackages = analyzePackageLevel();
        DependencyRoot dependencyRoot = dependencyRootAnalyser.analyze(analyzedPackages);
        StatsPrinter.print(dependencyRoot);
        draw(dependencyRoot, "PackageDependency");
    }

    public void findCyclicalDependencyPackages(String sourcePath, String pathSeparator) {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceTree(new File(sourcePath + rootPackage.replace(".", pathSeparator)));
        Stream.of(builder.getSources()
                        .stream()
                        .map(JavaSource::getPackageName)
                        .distinct()
                , Stream.of(rootPackage))
                .flatMap(identity())
                .map(packageName -> Tuple.of(packageName, analyzePackageLevel()))
                .filter(this::onlyCyclical)
                .map(Tuple2::_1)
                .forEach(System.out::println);
    }

    private boolean onlyCyclical(Tuple2<String, DependencyRoot> dependencyRootTuple2) {
        return dependencyRootTuple2._2().getDependencyNodes().stream()
                .anyMatch(packageAnalysis -> packageAnalysis
                        .getDependencies().stream().anyMatch(Dependency::isCyclical));
    }

    private DependencyRoot analyzePackageLevel() {
        DependencyRoot dependencyRoot = dependencyBuilder.analyze(rootPackage);
        dependencyRoot = packageAnalyser.analyze(dependencyRoot);
        return cyclicalDependencyAnalyser.analyze(dependencyRoot);
    }

}
