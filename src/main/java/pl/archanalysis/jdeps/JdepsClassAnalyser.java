package pl.archanalysis.jdeps;

import lombok.RequiredArgsConstructor;
import pl.archanalysis.core.ClassAnalyser;
import pl.archanalysis.core.Dependency;
import pl.archanalysis.core.analysis.ClassAnalysis;
import pl.archanalysis.core.analysis.DependencyAnalysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.mapping;

@RequiredArgsConstructor
public class JdepsClassAnalyser implements ClassAnalyser {

    private final String sourcePath;
    private final String pathSeparator;

    @Override
    public List<DependencyAnalysis> analyze(String codePath) {
        try {
            Map<String, List<String>> depsMap = readDependencies();
            return Stream.of(depsMap.values().stream().flatMap(Collection::stream), depsMap.keySet().stream())
                    .flatMap(Function.identity())
                    .distinct()
                    .map(clazz -> new ClassAnalysis(
                            clazz,
                            depsMap.getOrDefault(clazz, Collections.emptyList()).stream()
                                    .map(name -> Dependency.builder()
                                            .name(name)
                                            .count(1)
                                            .build())
                                    .collect(Collectors.toList())))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, List<String>> readDependencies() throws IOException {
        Runtime rt = Runtime.getRuntime();
        String[] commands = {"D:\\Program Files\\Java\\jdk-11\\bin\\jdeps", "-verbose:class", "-e", "\"pl.demo.*\"", "-R", "build/libs/archanalysis-0.0.1-SNAPSHOT.jar"};
        Process proc = rt.exec(commands);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

        List<String> errors = stdError.lines()
                .collect(Collectors.toList());
        if (!errors.isEmpty()) {
            throw new RuntimeException(errors.toString());
        }

        return stdInput.lines()
                .skip(1)
                .map(line -> Stream.of(line.split("->")).map(String::trim).collect(Collectors.toList()))
                .collect(Collectors.groupingBy(l -> l.get(0), mapping(l -> l.get(1).split(" ")[0], Collectors.toList())));
    }
}
