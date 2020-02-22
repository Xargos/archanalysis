package pl.archanalysis.core.analysers;

import pl.archanalysis.core.model.DependencyRoot;

public interface Analyser {
    DependencyRoot analyze(DependencyRoot dependencyRoot);
}
