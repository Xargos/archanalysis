package pl.archanalysis;

import pl.archanalysis.core.ArchAnalysis;
import pl.archanalysis.core.ClassAnalyser;
import pl.archanalysis.core.PackageAnalyser;
import pl.archanalysis.jdeps.JdepsClassAnalyser;
import pl.archanalysis.qdox.QdoxClassAnalyser;
import pl.archanalysis.qdox.QdoxPackageAnalyser;

import java.io.IOException;

public class App {

    public static void main(String... args) throws IOException {
        String codePackage = "pl.demo";
        String sourcePath = "D:\\Programowanie\\archanalysis\\src\\main\\java\\";
        String pathSeparator = "\\";

//        ClassAnalyser classAnalyser = new QdoxClassAnalyser(sourcePath, pathSeparator);
        ClassAnalyser classAnalyser = new JdepsClassAnalyser(sourcePath, pathSeparator);
        PackageAnalyser packageAnalyser = new QdoxPackageAnalyser(sourcePath, pathSeparator);
        ArchAnalysis archAnalysis = new ArchAnalysis(classAnalyser, packageAnalyser);

        archAnalysis.drawClassDependencyGraph(codePackage);
    }
}
