package pl.archanalysis.core;

import pl.archanalysis.model.DependencyRoot;

import java.util.Set;

public interface DependencyBuilder {
    DependencyRoot analyze(String codePath);

    DependencyRoot analyze(String codePath, Set<String> ignoreClass);
}
