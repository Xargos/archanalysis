package pl.archanalysis;

import pl.archanalysis.core.ClassAnalysis;
import pl.archanalysis.core.PackageAnalysis;
import pl.archanalysis.core.DependencyBuilder;
import pl.archanalysis.core.analysers.AnalyserFactory;
import pl.archanalysis.jdeps.JdepsDependencyBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class App {

    public static void main(String... args) throws IOException {
//        String rootPackage = "com.alibaba.fastjson";
//        String sourcePath = "D:\\Programowanie\\archanalysis\\src\\main\\java\\";
//        String pathSeparator = "\\";
//
////        ClassAnalyser classAnalyser = new QdoxClassAnalyser(sourcePath, pathSeparator);
//        DependencyAnalyser classAnalyser = new JdepsDependencyAnalyser(
//                rootPackage,
//                "D:\\Programowanie\\fastjson\\target\\fastjson-1.2.63_preview_01.jar",
//                "D:\\Program Files\\Java\\jdk-11\\bin\\jdeps");

        String rootPackage = "pl.archanalysis";
        String sourcePath = "D:\\Programowanie\\archanalysis\\src\\main\\java\\";
        String pathSeparator = "\\";

//        ClassAnalyser classAnalyser = new QdoxClassAnalyser(sourcePath, pathSeparator);
        DependencyBuilder classAnalyser = new JdepsDependencyBuilder(
                rootPackage,
                "build/libs/archanalysis-0.0.1-SNAPSHOT.jar",
                "D:\\Program Files\\Java\\jdk-11\\bin\\jdeps");

        Set<String> ignoreClass = Set.of(
                "pl.archanalysis.App",
                "pl.archanalysis.core.PackageAnalysis"
        );

        ClassAnalysis classAnalysis = new ClassAnalysis(
                rootPackage,
                classAnalyser,
                ignoreClass,
                List.of(AnalyserFactory.newDependencyRootAnalyser(), AnalyserFactory.newCyclicalDependencyAnalyser()));

        long start = System.nanoTime();
        classAnalysis.drawClassDependencyGraph();
        System.out.println("Finished in: " + (System.nanoTime() - start) / 1_000_000_000D);

    }
}
