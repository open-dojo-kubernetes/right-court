package br.pegz.tutorials.rightcourt.persistence.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public enum Height {
    BURNT, LOW, MEDIUM, HIGH, BEYOND_REACH;

    private static final List<Height> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static Height random()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}
