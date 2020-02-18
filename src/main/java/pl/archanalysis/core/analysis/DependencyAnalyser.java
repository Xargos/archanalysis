package pl.archanalysis.core.analysis;

import java.util.List;

public interface DependencyAnalyser {
    List<DependencyAnalysis> analyze(String codePath);
}
