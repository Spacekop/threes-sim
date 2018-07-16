package com.discoshiny.threes;

public enum CardinalDirection {
    UP(Alignment.VERTICAL, -1),
    RIGHT(Alignment.HORIZONTAL, 1),
    DOWN(Alignment.VERTICAL, 1),
    LEFT(Alignment.HORIZONTAL, -1);

    private final Alignment alignment;
    private final Integer direction;

    CardinalDirection(Alignment alignment, Integer direction) {
        this.alignment = alignment;
        this.direction = direction;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public Integer getDirection() {
        return direction;
    }
}
