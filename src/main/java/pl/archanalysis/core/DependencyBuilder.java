package pl.archanalysis.core;

public interface DependencyBuilder {
    DependencyRoot analyze(String codePath);
}
