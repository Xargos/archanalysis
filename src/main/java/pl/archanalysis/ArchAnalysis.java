package pl.archanalysis;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaSource;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import pl.archanalysis.clazz.ClassAnalyser;
import pl.archanalysis.pack.PackageAnalyser;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static pl.archanalysis.CircularDependencyAnalyzer.newCircularAnalyzer;
import static pl.archanalysis.DependencyDrawer.draw;

public class ArchAnalysis {

    public static void drawClassDependencyGraph(String rootPackage, String sourcePath, String pathSeparator) throws IOException {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceTree(new File(sourcePath + rootPackage.replace(".", pathSeparator)));
        List<DependencyAnalysis> analyses = ClassAnalyser.analyze(rootPackage, builder);
        draw(newCircularAnalyzer(analyses).analyze(), "ClassDependency");
    }

    public static void drawPackageDependencyGraph(String rootPackage, String sourcePath, String pathSeparator) throws IOException {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceTree(new File(sourcePath + rootPackage.replace(".", pathSeparator)));

        List<DependencyAnalysis> analyzedPackages = analyzePackageLevel(rootPackage, builder);

        draw(analyzedPackages, "PackageDependency");
    }

    public static void findCircularDependencyPackages(String rootPackage, String sourcePath, String pathSeparator) {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceTree(new File(sourcePath + rootPackage.replace(".", pathSeparator)));
        Stream.of(builder.getSources()
                        .stream()
                        .map(JavaSource::getPackageName)
                        .distinct()
                , Stream.of(rootPackage))
                .flatMap(identity())
                .map(packageName -> Tuple.of(packageName, analyzePackageLevel(packageName, builder)))
                .filter(ArchAnalysis::onlyCircular)
                .map(Tuple2::_1)
                .forEach(System.out::println);
    }

    private static boolean onlyCircular(Tuple2<String, List<DependencyAnalysis>> packageAnalyses) {
        return packageAnalyses._2().stream()
                .anyMatch(packageAnalysis -> packageAnalysis
                        .getDependencies().stream().anyMatch(Dependency::isCircular));
    }

    private static List<DependencyAnalysis> analyzePackageLevel(String rootPackage, JavaProjectBuilder builder) {
        int level = rootPackage.split("\\.").length + 1;
        List<DependencyAnalysis> packageAnalyses = PackageAnalyser.analyze(rootPackage, level, builder);
        return newCircularAnalyzer(packageAnalyses).analyze();
    }

}
