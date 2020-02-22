package pl.archanalysis.analysers;

import pl.archanalysis.model.DependencyRoot;

public interface Analyser {
    DependencyRoot analyze(DependencyRoot dependencyRoot);
}
