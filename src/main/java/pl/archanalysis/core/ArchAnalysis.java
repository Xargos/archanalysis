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
import static pl.archanalysis.core.CircularDependencyAnalyzer.newCircularAnalyzer;
import static pl.archanalysis.core.DependencyDrawer.draw;

@RequiredArgsConstructor
public class ArchAnalysis {

    private final DependencyAnalyser classAnalyser;

    public void drawClassDependencyGraph(String rootPackage) throws IOException {
        List<DependencyAnalysis> analyses = classAnalyser.analyze(rootPackage);
        DependencyAnalysisRoot dependencyAnalysisRoot = DependencyRootAnalyser.analyzeRoot(newCircularAnalyzer(analyses).analyze());
        draw(dependencyAnalysisRoot, "ClassDependency");
    }

    public void drawPackageDependencyGraph(String rootPackage) throws IOException {
        List<DependencyAnalysis> analyzedPackages = analyzePackageLevel(rootPackage);
        DependencyAnalysisRoot dependencyAnalysisRoot = DependencyRootAnalyser.analyzeRoot(analyzedPackages);
        draw(dependencyAnalysisRoot, "PackageDependency");
    }

    public void findCircularDependencyPackages(String rootPackage, String sourcePath, String pathSeparator) {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceTree(new File(sourcePath + rootPackage.replace(".", pathSeparator)));
        Stream.of(builder.getSources()
                        .stream()
                        .map(JavaSource::getPackageName)
                        .distinct()
                , Stream.of(rootPackage))
                .flatMap(identity())
                .map(packageName -> Tuple.of(packageName, analyzePackageLevel(packageName)))
                .filter(this::onlyCircular)
                .map(Tuple2::_1)
                .forEach(System.out::println);
    }

    private boolean onlyCircular(Tuple2<String, List<DependencyAnalysis>> packageAnalyses) {
        return packageAnalyses._2().stream()
                .anyMatch(packageAnalysis -> packageAnalysis
                        .getDependencies().stream().anyMatch(Dependency::isCircular));
    }

    private List<DependencyAnalysis> analyzePackageLevel(String rootPackage) {
        List<DependencyAnalysis> classAnalyses = classAnalyser.analyze(rootPackage);
        List<DependencyAnalysis> packageAnalyses = PackageAnalyser.analyze(rootPackage, classAnalyses);
        return newCircularAnalyzer(packageAnalyses).analyze();
    }

}
