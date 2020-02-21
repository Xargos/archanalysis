package pl.archanalysis.jdeps;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import lombok.RequiredArgsConstructor;
import pl.archanalysis.core.Dependency;
import pl.archanalysis.core.DependencyBuilder;
import pl.archanalysis.core.DependencyNode;
import pl.archanalysis.core.DependencyRoot;

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
public class JdepsDependencyBuilder implements DependencyBuilder {

    private final String packageName;
    private final String jar;
    private final String jdeps;

    @Override
    public DependencyRoot analyze(String codePath) {
        try {
            Map<String, List<String>> depsMap = readDependencies();
            return DependencyRoot.builder().dependencyNodes(buildDependencyAnalysis(depsMap)).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<DependencyNode> buildDependencyAnalysis(Map<String, List<String>> depsMap) {
        return Stream.of(depsMap.values().stream().flatMap(Collection::stream), depsMap.keySet().stream())
                .flatMap(Function.identity())
                .distinct()
                .map(dep -> new DependencyNode(
                        dep,
                        depsMap.getOrDefault(dep, Collections.emptyList()).stream()
                                .reduce(HashMap.empty(), JdepsDependencyBuilder::mergeRaw, HashMap::merge)
                                .map(params -> Dependency.builder()
                                        .name(params._1())
                                        .count(params._2())
                                        .build())
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    private Map<String, List<String>> readDependencies() throws IOException {
        Runtime rt = Runtime.getRuntime();
        String[] commands = {jdeps, "-verbose:class", "-e", packageName + ".*", "-R", jar};
        Process proc = rt.exec(commands);

        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

//        BufferedReader stdError = new BufferedReader(new
//                InputStreamReader(proc.getErrorStream()));

//        List<String> errors = stdError.lines()
//                .collect(Collectors.toList());
//        if (!errors.isEmpty()) {
//            throw new RuntimeException(errors.toString());
//        }

        return parseJdeps(stdInput);
    }

    private Map<String, List<String>> parseJdeps(BufferedReader stdInput) {
        return stdInput.lines()
                .skip(1)
                .map(line -> Stream.of(line.split("->")).map(String::trim).collect(Collectors.toList()))
                .map(l -> Tuple.of(l.get(0).split("\\$")[0], l.get(1).split(" ")[0].split("\\$")[0]))
                .collect(Collectors.groupingBy(Tuple2::_1, mapping(Tuple2::_2, Collectors.toList())));
    }

    private static HashMap<String, Integer> mergeRaw(HashMap<String, Integer> map, String name) {
        return map.put(name,
                map.get(name)
                        .map(count -> count + 1)
                        .getOrElse(() -> 1));
    }
}