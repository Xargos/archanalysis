package pl.archanalysis;

import com.thoughtworks.qdox.JavaProjectBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static pl.archanalysis.CircularDependencyAnalyzer.newAnalyzer;
import static pl.archanalysis.DependencyDrawer.draw;

public class ArchAnalysis {

    public static void analyze(String codePackage, String sourcePath, String pathSeparator) throws IOException {
        int level = codePackage.split("\\.").length + 1;
        JavaProjectBuilder builder = new JavaProjectBuilder();
        builder.addSourceTree(new File(sourcePath + codePackage.replace(".", pathSeparator)));
        List<PackageAnalysis> packageAnalyses = PackageAnalyser.analyze(codePackage, level, builder);
        List<PackageAnalysis> analyzedPackages = newAnalyzer(packageAnalyses).analyze();

        draw(analyzedPackages);
    }


}
