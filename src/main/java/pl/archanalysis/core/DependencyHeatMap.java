package pl.archanalysis.core;

import guru.nidi.graphviz.attribute.Color;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import pl.archanalysis.core.analysis.DependencyAnalysisRoot;

import static io.vavr.API.*;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DependencyHeatMap {
    private final DependencyAnalysisRoot dependencyAnalysisRoot;
    private final HeatMapType heatMapType;

    public enum HeatMapType {
        DEPEND_UPON,
        DEPEND_ON,
        DEPEND_SUM
    }

    public static DependencyHeatMap dependOnHeatMap(DependencyAnalysisRoot dependencyAnalysisRoot) {
        return new DependencyHeatMap(dependencyAnalysisRoot, HeatMapType.DEPEND_ON);
    }

    public static DependencyHeatMap dependUponHeatMap(DependencyAnalysisRoot dependencyAnalysisRoot) {
        return new DependencyHeatMap(dependencyAnalysisRoot, HeatMapType.DEPEND_UPON);
    }

    public static DependencyHeatMap dependSumHeatMap(DependencyAnalysisRoot dependencyAnalysisRoot) {
        return new DependencyHeatMap(dependencyAnalysisRoot, HeatMapType.DEPEND_SUM);
    }

    Color heatMap(long dependsUpon, int dependsOn) {
        return Match(heatMapType).of(
                Case($(HeatMapType.DEPEND_SUM), () -> heatMap(dependencyAnalysisRoot.getMaxDependsSum(), dependsUpon + dependsOn)),
                Case($(HeatMapType.DEPEND_UPON), () -> heatMap(dependencyAnalysisRoot.getMaxDependsUpon(), dependsUpon)),
                Case($(HeatMapType.DEPEND_ON), () -> heatMap(dependencyAnalysisRoot.getMaxDependsOn(), dependsOn)));
    }

    private Color heatMap(long max, long value) {
        int heat = (int) ((value * 255D) / max);
        return Color.rgb(heat, 0, 0);
    }
}
