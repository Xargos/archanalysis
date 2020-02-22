package pl.archanalysis.core.analysers;

import pl.archanalysis.core.model.DependencyRoot;

public class CyclicalDependencyAnalyser implements Analyser {

    @Override
    public DependencyRoot analyze(DependencyRoot dependencyRoot) {
        return dependencyRoot.toBuilder()
                .dependencyNodes(StatefulCyclicalDependencyAnalyser.newCyclicalAnalyzer(dependencyRoot).analyze())
                .build();
    }
}
