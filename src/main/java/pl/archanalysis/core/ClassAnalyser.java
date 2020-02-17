package pl.archanalysis.core;

import pl.archanalysis.core.analysis.DependencyAnalysis;

import java.util.List;

public interface ClassAnalyser {
    List<DependencyAnalysis> analyze(String codePath);
}
