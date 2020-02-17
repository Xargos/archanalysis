package pl.demo.infrastructure.other;

import pl.demo.TopLevel;
import pl.demo.infrastructure.SomeRepository;
import pl.demo.infrastructure.either.OtherRepository2;

public class OtherRepository {
    //    private Demo demo;
    private SomeRepository someRepository;
    private OtherRepository2 otherRepository;
    private TopLevel topLevel;
}

//"D:\Program Files\Java\jdk-11\bin\jdeps" -verbose:class -e "pl.demo.*" -R --dot-output dots build/libs/archanalysis-0.0.1-SNAPSHOT.jar
//        $ dot -Tpng -O dots/summary.dot
