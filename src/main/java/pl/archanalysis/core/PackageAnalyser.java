package pl.archanalysis.core;

import pl.archanalysis.core.analysis.DependencyAnalysis;

import java.util.List;

public interface PackageAnalyser {
    List<DependencyAnalysis> analyze(String rootPackage);
}
