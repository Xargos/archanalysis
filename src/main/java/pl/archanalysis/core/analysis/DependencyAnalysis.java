package pl.archanalysis.core.analysis;

import pl.archanalysis.core.Dependency;

import java.util.List;

public interface DependencyAnalysis {
    DependencyAnalysis copy();

    String getName();

    List<Dependency> getDependencies();

    DependencyAnalysis copyWithPackageDependencies(List<Dependency> dependencies);
}
