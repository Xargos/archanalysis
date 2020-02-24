package pl.archanalysis.analysers;

public interface AnalyserFactory {
    static Analyser newPackageAnalyser(String rootPackage) {
        return new PackageAnalyser(rootPackage);
    }

    static Analyser newDependencyRootAnalyser() {
        return new DependencyRootAnalyser();
    }

    static Analyser newCyclicalDependencyAnalyser() {
        return new CyclicalDependencyAnalyser();
    }

    static Analyser newModuleMarkerAnalyser() {
        return new ModuleMarkerAnalyser();
    }
}
