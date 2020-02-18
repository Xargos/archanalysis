package pl.archanalysis;

import pl.archanalysis.core.ArchAnalysis;
import pl.archanalysis.core.analysis.DependencyAnalyser;
import pl.archanalysis.jdeps.JdepsDependencyAnalyser;

import java.io.IOException;

public class App {

    public static void main(String... args) throws IOException {
        String codePackage = "pl.demo";
        String sourcePath = "D:\\Programowanie\\archanalysis\\src\\main\\java\\";
        String pathSeparator = "\\";

//        ClassAnalyser classAnalyser = new QdoxClassAnalyser(sourcePath, pathSeparator);
        DependencyAnalyser classAnalyser = new JdepsDependencyAnalyser(sourcePath, pathSeparator);
        ArchAnalysis archAnalysis = new ArchAnalysis(classAnalyser);

        archAnalysis.drawPackageDependencyGraph(codePackage);
    }
}
