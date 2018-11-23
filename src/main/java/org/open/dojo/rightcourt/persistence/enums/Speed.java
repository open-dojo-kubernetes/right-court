package org.open.dojo.rightcourt.persistence.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum  Speed {
    SLOW, AVG, FAST, OMFG;

    private static final List<Speed> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static Speed random()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}
