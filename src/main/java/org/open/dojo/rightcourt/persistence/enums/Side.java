package org.open.dojo.rightcourt.persistence.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Side {
    RIGHT, LEFT, NET, OUTSIDE;

    private static final List<Side> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static Side random()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}
