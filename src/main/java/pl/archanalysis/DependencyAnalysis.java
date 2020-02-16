package pl.archanalysis;

import java.util.List;

public interface DependencyAnalysis {
    DependencyAnalysis copy();

    String getName();

    List<Dependency> getDependencies();

    DependencyAnalysis copyWithPackageDependencies(List<Dependency> dependencies);
}
