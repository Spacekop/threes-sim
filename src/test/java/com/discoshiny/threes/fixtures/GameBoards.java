package com.discoshiny.threes.fixtures;

public class GameBoards {
    public static Integer[] GOOD_MIX = new Integer[] {
            null, null, 0, 0,
            null, 0, null, 0,
            null, null, 1, 0,
            2, 2, 3, null
    };

    public static Integer[] NO_MOVES = new Integer[] {
            0, 1, -1, 2,
            3, -1, 1, 0,
            -2, 5, 0, -2,
            0, -2, 1, 0
    };

    public static Integer[] WHAT_GIVES = new Integer[] {
            null, null, -2, 1,
            null, null, null, 2,
            null, null, 0, 3,
            0, 0, -2, 1
    };
}
