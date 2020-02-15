package pl.archanalysis.clazz;

import com.thoughtworks.qdox.model.JavaSource;
import lombok.Value;

import java.util.Set;

@Value
public class ClassAnalysis {
    private final JavaSource javaSource;
    private final Set<String> packageDependencies;

    @Override
    public String toString() {
        return "ClassAnalysis{" +
                "class=" + javaSource.getClasses().get(0) +
                ", packageDependencies=" + packageDependencies +
                '}';
    }

    public String getPackageName() {
        return javaSource.getPackageName();
    }
}
