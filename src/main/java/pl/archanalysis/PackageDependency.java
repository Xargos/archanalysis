package pl.archanalysis;


import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class PackageDependency {
    private final String name;
    private final Integer count;
    private boolean circular;
}
