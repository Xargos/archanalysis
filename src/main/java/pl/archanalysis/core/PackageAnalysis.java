package pl.archanalysis.core;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaSource;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import pl.archanalysis.analysers.Analyser;
import pl.archanalysis.analysers.CyclicalDependencyAnalyser;
import pl.archanalysis.analysers.DependencyRootAnalyser;
import pl.archanalysis.model.Dependency;
import pl.archanalysis.model.DependencyRoot;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static pl.archanalysis.core.DependencyDrawer.draw;

public class PackageAnalysis {

    private final String rootPackage;
    private final DependencyBuilder dependencyBuilder;
    private final Set<String> ignoreClass;
    private final Analyser packageAnalyser;
    private final DependencyRootAnalyser dependencyRootAnalyser = new DependencyRootAnalyser();
    private final CyclicalDependencyAnalyser cyclicalDependencyAnalyser = new CyclicalDependencyAnalyser();

    public PackageAnalysis(String rootPackage,
                           DependencyBuilder dependencyBuilder,
                           Set<String> ignoreClass,
                           Analyser packageAnalyser) {
        this.rootPackage = rootPackage;
        this.dependencyBuilder = dependencyBuilder;
        this.ignoreClass = ignoreClass;
        this.packageAnalyser = packageAnalyser;
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
        return dependencyRootTuple2._2().getDependencyNodes().values().stream()
                .anyMatch(packageAnalysis -> packageAnalysis
                        .getDependencies().stream().anyMatch(Dependency::isCyclical));
    }

    private DependencyRoot analyzePackageLevel() {
        DependencyRoot dependencyRoot = dependencyBuilder.analyze(rootPackage);
        dependencyRoot = packageAnalyser.analyze(dependencyRoot);
        return cyclicalDependencyAnalyser.analyze(dependencyRoot);
    }

}
