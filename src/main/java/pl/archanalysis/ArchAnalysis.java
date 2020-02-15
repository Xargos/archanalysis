package pl.archanalysis;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaSource;
import io.vavr.Tuple;
import io.vavr.Tuple2;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static pl.archanalysis.CircularDependencyAnalyzer.newAnalyzer;
import static pl.archanalysis.DependencyDrawer.draw;

public class ArchAnalysis {

    public static void drawPackageDependencyGraph(String rootPackage, String sourcePath, String pathSeparator) throws IOException {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceTree(new File(sourcePath + rootPackage.replace(".", pathSeparator)));

        List<PackageAnalysis> analyzedPackages = analyzePackageLevel(rootPackage, builder);

        draw(analyzedPackages);
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

    private static boolean onlyCircular(Tuple2<String, List<PackageAnalysis>> packageAnalyses) {
        return packageAnalyses._2().stream()
                .anyMatch(packageAnalysis -> packageAnalysis
                        .getPackageDependencies().stream().anyMatch(PackageDependency::isCircular));
    }

    private static List<PackageAnalysis> analyzePackageLevel(String rootPackage, JavaProjectBuilder builder) {
        int level = rootPackage.split("\\.").length + 1;
        List<PackageAnalysis> packageAnalyses = PackageAnalyser.analyze(rootPackage, level, builder);
        return newAnalyzer(packageAnalyses).analyze();
    }

}
