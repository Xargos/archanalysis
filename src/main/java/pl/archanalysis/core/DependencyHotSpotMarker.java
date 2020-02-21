package pl.archanalysis.core;

import guru.nidi.graphviz.attribute.Color;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import static io.vavr.API.*;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DependencyHotSpotMarker {
    private final RootAnalytics rootAnalytics;
    private final HeatMapType heatMapType;

    public enum HeatMapType {
        DEPEND_UPON,
        DEPEND_ON,
        DEPEND_ALL
    }

    public static DependencyHotSpotMarker dependOnHotSpot(RootAnalytics rootAnalytics) {
        return new DependencyHotSpotMarker(rootAnalytics, HeatMapType.DEPEND_ON);
    }

    public static DependencyHotSpotMarker dependUponHotSpot(RootAnalytics rootAnalytics) {
        return new DependencyHotSpotMarker(rootAnalytics, HeatMapType.DEPEND_UPON);
    }

    public static DependencyHotSpotMarker dependAllHotSpot(RootAnalytics rootAnalytics) {
        return new DependencyHotSpotMarker(rootAnalytics, HeatMapType.DEPEND_ALL);
    }

    Color heatMap(long dependsUpon, int dependsOn) {
        return Match(heatMapType).of(
                Case($(HeatMapType.DEPEND_ALL), () -> heatMap(rootAnalytics.getAllDepends(), dependsUpon + dependsOn)),
                Case($(HeatMapType.DEPEND_UPON), () -> heatMap(rootAnalytics.getDependsUpon(), dependsUpon)),
                Case($(HeatMapType.DEPEND_ON), () -> heatMap(rootAnalytics.getDependsOn(), dependsOn)));
    }

    private Color heatMap(DescriptiveStatistics descriptiveStatistics, long value) {
        double mean = descriptiveStatistics.getMean();
        double stddev = descriptiveStatistics.getStandardDeviation();
        double distance = mean + (stddev * 2);
        return Color.rgb(value > distance ? 255 : 0, 0, 0);
    }
}
