package pl.archanalysis.core;

import pl.archanalysis.core.model.DependencyRoot;

public interface DependencyBuilder {
    DependencyRoot analyze(String codePath);
}
