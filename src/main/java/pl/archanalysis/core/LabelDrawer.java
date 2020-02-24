package pl.archanalysis.core;

import guru.nidi.graphviz.attribute.Label;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import pl.archanalysis.model.RootAnalytics;

import static io.vavr.API.*;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LabelDrawer {
    private final RootAnalytics rootAnalytics;
    private final HeatMapType heatMapType;

    public enum HeatMapType {
        DEPEND_UPON,
        DEPEND_ON,
        DEPEND_ALL
    }

    public static LabelDrawer dependOnHotSpot(RootAnalytics rootAnalytics) {
        return new LabelDrawer(rootAnalytics, HeatMapType.DEPEND_ON);
    }

    public static LabelDrawer dependUponHotSpot(RootAnalytics rootAnalytics) {
        return new LabelDrawer(rootAnalytics, HeatMapType.DEPEND_UPON);
    }

    public static LabelDrawer dependAllHotSpot(RootAnalytics rootAnalytics) {
        return new LabelDrawer(rootAnalytics, HeatMapType.DEPEND_ALL);
    }

    Label draw(String name, long dependsUpon, int dependsOn) {
        String label = name + " " + dependsOn + "/" + dependsUpon;
        return Match(heatMapType).of(
                Case($(HeatMapType.DEPEND_ALL), () -> draw(rootAnalytics.getAllDepends(), dependsUpon + dependsOn, label)),
                Case($(HeatMapType.DEPEND_UPON), () -> draw(rootAnalytics.getDependsUpon(), dependsUpon, label)),
                Case($(HeatMapType.DEPEND_ON), () -> draw(rootAnalytics.getDependsOn(), dependsOn, label)));
    }

    private Label draw(DescriptiveStatistics descriptiveStatistics, long value, String label) {
        double mean = descriptiveStatistics.getMean();
        double stddev = descriptiveStatistics.getStandardDeviation();
        double distance = mean + (stddev * 2);
        if (value > distance) {
            return Label.html("<font color=\"red\">" + label + "</font>");
        }
        return Label.html(label);
    }
}
