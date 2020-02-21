package pl.archanalysis.core;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaSource;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import pl.archanalysis.core.analysis.DependencyAnalyser;
import pl.archanalysis.core.analysis.DependencyAnalysis;
import pl.archanalysis.core.analysis.DependencyAnalysisRoot;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static pl.archanalysis.core.CyclicalDependencyAnalyzer.newCyclicalAnalyzer;
import static pl.archanalysis.core.DependencyDrawer.draw;

@RequiredArgsConstructor
public class ArchAnalysis {

    private final DependencyAnalyser dependencyAnalyser;

    public void drawClassDependencyGraph(String rootPackage) throws IOException {
        List<DependencyAnalysis> analyses = dependencyAnalyser.analyze(rootPackage);
        List<DependencyAnalysis> cyclicalAnalyzed = newCyclicalAnalyzer(analyses).analyze();
        DependencyAnalysisRoot dependencyAnalysisRoot = DependencyRootAnalyser.analyzeRoot(cyclicalAnalyzed);
        StatsPrinter.print(dependencyAnalysisRoot);
        draw(dependencyAnalysisRoot, "ClassDependency");
    }

    public void drawPackageDependencyGraph(String rootPackage) throws IOException {
        List<DependencyAnalysis> analyzedPackages = analyzePackageLevel(rootPackage);
        DependencyAnalysisRoot dependencyAnalysisRoot = DependencyRootAnalyser.analyzeRoot(analyzedPackages);
        StatsPrinter.print(dependencyAnalysisRoot);
        draw(dependencyAnalysisRoot, "PackageDependency");
    }

    public void findCyclicalDependencyPackages(String rootPackage, String sourcePath, String pathSeparator) {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceTree(new File(sourcePath + rootPackage.replace(".", pathSeparator)));
        Stream.of(builder.getSources()
                        .stream()
                        .map(JavaSource::getPackageName)
                        .distinct()
                , Stream.of(rootPackage))
                .flatMap(identity())
                .map(packageName -> Tuple.of(packageName, analyzePackageLevel(packageName)))
                .filter(this::onlyCyclical)
                .map(Tuple2::_1)
                .forEach(System.out::println);
    }

    private boolean onlyCyclical(Tuple2<String, List<DependencyAnalysis>> packageAnalyses) {
        return packageAnalyses._2().stream()
                .anyMatch(packageAnalysis -> packageAnalysis
                        .getDependencies().stream().anyMatch(Dependency::isCyclical));
    }

    private List<DependencyAnalysis> analyzePackageLevel(String rootPackage) {
        List<DependencyAnalysis> classAnalyses = dependencyAnalyser.analyze(rootPackage);
        List<DependencyAnalysis> packageAnalyses = PackageAnalyser.analyze(rootPackage, classAnalyses);
        return newCyclicalAnalyzer(packageAnalyses).analyze();
    }

}
