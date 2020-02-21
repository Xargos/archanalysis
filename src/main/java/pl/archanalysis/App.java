package pl.archanalysis;

import pl.archanalysis.core.ArchAnalysis;
import pl.archanalysis.core.analysis.DependencyAnalyser;
import pl.archanalysis.jdeps.JdepsDependencyAnalyser;

import java.io.IOException;
import java.util.Set;

public class App {

    public static void main(String... args) throws IOException {
//        String codePackage = "com.alibaba.fastjson";
//        String sourcePath = "D:\\Programowanie\\archanalysis\\src\\main\\java\\";
//        String pathSeparator = "\\";
//
////        ClassAnalyser classAnalyser = new QdoxClassAnalyser(sourcePath, pathSeparator);
//        DependencyAnalyser classAnalyser = new JdepsDependencyAnalyser(
//                codePackage,
//                "D:\\Programowanie\\fastjson\\target\\fastjson-1.2.63_preview_01.jar",
//                "D:\\Program Files\\Java\\jdk-11\\bin\\jdeps");

        String codePackage = "pl.archanalysis";
        String sourcePath = "D:\\Programowanie\\archanalysis\\src\\main\\java\\";
        String pathSeparator = "\\";

//        ClassAnalyser classAnalyser = new QdoxClassAnalyser(sourcePath, pathSeparator);
        DependencyAnalyser classAnalyser = new JdepsDependencyAnalyser(
                codePackage,
                "build/libs/archanalysis-0.0.1-SNAPSHOT.jar",
                "D:\\Program Files\\Java\\jdk-11\\bin\\jdeps");

        Set<String> ignoreClass = Set.of(
                "pl.archanalysis.App",
                "pl.archanalysis.core.ArchAnalysis");

        ArchAnalysis archAnalysis = new ArchAnalysis(classAnalyser, ignoreClass);

        long start = System.nanoTime();
        archAnalysis.drawClassDependencyGraph(codePackage);
        System.out.println("Finished in: " + (System.nanoTime() - start) / 1_000_000_000D);

    }
}
