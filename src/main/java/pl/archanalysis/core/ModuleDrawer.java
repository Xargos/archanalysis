package pl.archanalysis.core;

import guru.nidi.graphviz.attribute.Color;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import pl.archanalysis.model.DependencyNode;
import pl.archanalysis.model.DependencyRoot;
import pl.archanalysis.model.RootAnalytics;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ModuleDrawer {
    private final Map<String, Integer> moduleColorMapping;
    private final Map<String, String> predictedModule;

    static ModuleDrawer newModuleDrawer(DependencyRoot dependencyRoot) {
        RootAnalytics rootAnalytics = dependencyRoot.getRootAnalytics();
        int colorStep = 16777216 / (rootAnalytics.getRoots().size() + 2); // Black is for moduless plus we don't want to get to white color
        int color = colorStep;
        Map<String, Integer> moduleColorMapping = new HashMap<>();
        for (String root : getModuleNames(dependencyRoot, rootAnalytics)) {
            moduleColorMapping.put(root, color);
            color += colorStep;
        }

        return new ModuleDrawer(moduleColorMapping,
                dependencyRoot.getRootAnalytics().getPredictedModule());
    }

    private static Set<String> getModuleNames(DependencyRoot dependencyRoot, RootAnalytics rootAnalytics) {
        return rootAnalytics.getRoots().stream()
                .map(dependencyRoot::getDependencyNode)
                .map(DependencyNode::getPackageCanonicalName)
                .collect(Collectors.toSet());
    }

    public Color getColorForModule(String name) {
        return Color.rgb(moduleColorMapping.getOrDefault(predictedModule.get(name), 0));
    }


}
