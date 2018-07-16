package com.discoshiny.threes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;

public class Board {
    private static final Logger LOG = LoggerFactory.getLogger(Board.class);
    private static final CardinalDirection[] POSSIBLE_DIRECTIONS = CardinalDirection.values();

    public static final Integer ROWS_COLS = 4;

    private final TranslatedCardStack translatedCardStack;
    private final PrimeMover mover;
    private final Long seed;

    private final List<MoveRecord> moves = new ArrayList<>();

    public Board(Long seed, PrimeMover mover, Stack<Integer> cardStack, Integer[] board) {
        this.seed = seed;
        this.mover = mover;
        this.translatedCardStack = new TranslatedCardStack(cardStack);
        moves.add(MoveRecord.InitialState(board));
    }

    Integer[] getBoard() {
        return moves.get(moves.size() - 1).after;
    }

    public boolean canMove(CardinalDirection direction) {
        return PrimeMover.buildMoveInformation(getBoard(), direction).didMove();
    }

    public boolean canMove() {
        List<String> possibleMoves = new ArrayList<>();

        for (CardinalDirection direction : POSSIBLE_DIRECTIONS) {
            if (canMove(direction)) {
                possibleMoves.add(direction.toString());
            }
        }

        LOG.info("Possible moves: {}", String.join(", ", possibleMoves));

        return possibleMoves.size() > 0;
    }

    public boolean move(CardinalDirection direction) {
        if (!canMove(direction)) {
            return false;
        }

        Integer[] moved = mover.move(getBoard(), direction);

        if (Arrays.equals(getBoard(), moved)) {
            return false;
        }

        moves.add(new MoveRecord(direction, getBoard(), moved));
        return true;
    }

    public Integer getRankAt(Integer row, Integer col) {
        if (row >= ROWS_COLS || col >= ROWS_COLS) {
            throw new IndexOutOfBoundsException("row or col must be [0-3]");
        }

        return getBoard()[row * ROWS_COLS + col];
    }

    public Card getCardAt(Integer row, Integer col) {
        return new Card(getRankAt(row, col));
    }

    public Integer getScore() {
        return Arrays.stream(getBoard())
                .map(rank -> new Card(rank).getScore())
                .reduce(0, Integer::sum);
    }

    void logBoard() {
        LOG.info("Next: {}", translatedCardStack.peek().getValue());
        LOG.info("==============================");
        for (int i = 0; i < 4; i++) {
            LOG.info("| {} | {} | {} | {} |",
                    getCardAt(i, 0),
                    getCardAt(i, 1),
                    getCardAt(i, 2),
                    getCardAt(i, 3));
        }
        LOG.info("Score: {}", getScore());
    }

    SavedGame saveGame() {
        return new SavedGame(
                this.seed,
                moves.stream()
                        .map(MoveRecord::getDirection)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );
    }

    String moveHistory() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(moves);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board1 = (Board) o;
        return Arrays.equals(getBoard(), board1.getBoard());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getBoard());
    }

    private static class MoveRecord {
        private final CardinalDirection direction;
        private final Integer[] before;
        private final Integer[] after;

        public MoveRecord(CardinalDirection direction, Integer[] before, Integer[] after) {
            this.direction = direction;
            this.before = before;
            this.after = after;
        }

        public static MoveRecord InitialState(Integer[] board) {
            return new MoveRecord(null, null, board);
        }

        public CardinalDirection getDirection() {
            return direction;
        }

        @Override
        public String toString() {
            return "MoveRecord{" +
                    "direction=" + direction +
                    ", before=" + (before == null ? null : Arrays.toString(before)) +
                    ", after=" + (after == null ? null : Arrays.toString(after)) +
                    '}';
        }
    }
}
