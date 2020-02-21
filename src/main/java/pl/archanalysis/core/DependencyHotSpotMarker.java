package pl.archanalysis.core;

import guru.nidi.graphviz.attribute.Color;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import pl.archanalysis.core.analysis.DependencyAnalysisRoot;

import static io.vavr.API.*;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DependencyHotSpotMarker {
    private final DependencyAnalysisRoot dependencyAnalysisRoot;
    private final HeatMapType heatMapType;

    public enum HeatMapType {
        DEPEND_UPON,
        DEPEND_ON,
        DEPEND_SUM
    }

    public static DependencyHotSpotMarker dependOnHeatMap(DependencyAnalysisRoot dependencyAnalysisRoot) {
        return new DependencyHotSpotMarker(dependencyAnalysisRoot, HeatMapType.DEPEND_ON);
    }

    public static DependencyHotSpotMarker dependUponHeatMap(DependencyAnalysisRoot dependencyAnalysisRoot) {
        return new DependencyHotSpotMarker(dependencyAnalysisRoot, HeatMapType.DEPEND_UPON);
    }

    public static DependencyHotSpotMarker dependSumHeatMap(DependencyAnalysisRoot dependencyAnalysisRoot) {
        return new DependencyHotSpotMarker(dependencyAnalysisRoot, HeatMapType.DEPEND_SUM);
    }

    Color heatMap(long dependsUpon, int dependsOn) {
        return Match(heatMapType).of(
                Case($(HeatMapType.DEPEND_SUM), () -> heatMap(dependencyAnalysisRoot.getAllDepends(), dependsUpon + dependsOn)),
                Case($(HeatMapType.DEPEND_UPON), () -> heatMap(dependencyAnalysisRoot.getDependsUpon(), dependsUpon)),
                Case($(HeatMapType.DEPEND_ON), () -> heatMap(dependencyAnalysisRoot.getDependsOn(), dependsOn)));
    }

    private Color heatMap(DescriptiveStatistics descriptiveStatistics, long value) {
        double mean = descriptiveStatistics.getMean();
        double stddev = descriptiveStatistics.getStandardDeviation();
        double distance = mean + (stddev * 2);
        return Color.rgb(value > distance ? 255 : 0, 0, 0);
    }
}
