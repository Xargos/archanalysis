package pl.archanalysis;

import java.io.IOException;

public class App {

    public static void main(String... args) throws IOException {
        String codePackage = "pl.demo";
        String sourcePath = "D:\\Programowanie\\archanalysis\\src\\main\\java\\";
        String pathSeparator = "\\";
        ArchAnalysis.drawPackageDependencyGraph(codePackage, sourcePath, pathSeparator);
    }
}
