package com.discoshiny.threes;

public class Card {
    public static final Integer ONE_CARD_RANK = -1;
    public static final Integer TWO_CARD_RANK = -2;
    public static final Integer WILD_RANK = 99;

    private final Integer rank;

    public Card(Integer rank) {
        this.rank = rank;
    }

    public Integer getRank() {
        return rank;
    }

    public Integer getValue() {
        if (rank == null) {
            return null;
        }

        if (rank.equals(Card.WILD_RANK)) {
            return 9999;
        }

        if (rank < 0) {
            return Math.abs(rank);
        }

        return ((Double)Math.pow(2, rank)).intValue() * 3;
    }

    public Integer getScore() {
        if (rank == null || rank < 0) {
            return 0;
        }

        return ((Double)Math.pow(3, rank + 1)).intValue();
    }

    @Override
    public String toString() {
        if (rank == null) {
            return " -- ";
        }

        if (rank.equals(WILD_RANK)) {
            return " ?? ";
        }

        return String.format("%1$4d", getValue());
    }
}
