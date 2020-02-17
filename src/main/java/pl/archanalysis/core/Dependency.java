package pl.archanalysis.core;


import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Dependency {
    private final String name;
    private final Integer count;
    private boolean circular;
}
