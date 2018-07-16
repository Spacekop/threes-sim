package com.discoshiny.threes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Always try to move cards towards zero index. Align board such that direction of movement is towards zero.
 */
public class PrimeMover {
    private final Random random;
    private final Stack<Integer> cardStack;

    public PrimeMover(Stack<Integer> cardStack, Long seed) {
        this.cardStack = cardStack;
        this.random = Optional.ofNullable(seed)
                .map(Random::new)
                .orElse(new Random());
    }

    public PrimeMover(Stack<Integer> cardStack) {
        this(cardStack, null);
    }

    static Integer[][] toMatrix(Integer[] gameBoard, int rows, int cols) {
        Integer[][] matrix = new Integer[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                matrix[r][c] = gameBoard[r * cols + c];
            }
        }

        return matrix;
    }

    static Integer[] toArray(Integer[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        Integer[] gameBoard = new Integer[rows * cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                gameBoard[r * cols + c] = matrix[r][c];
            }
        }

        return gameBoard;
    }

    static Integer[][] transpose(Integer[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        Integer[][] transposed = new Integer[cols][rows];
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                transposed[col][row] = matrix[row][col];

        return transposed;
    }

    static Integer[][] reverseRows(Integer[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        Integer[][] reversed = new Integer[rows][cols];
        for (int row = 0; row < rows; row++) {
            reversed[row] = reverse(matrix[row]);
        }

        return reversed;
    }

    static Integer[] reverse(Integer[] array) {
        int length = array.length;
        Integer[] reversed =  new Integer[length];

        for (int i = 0; i < length; i++) {
            reversed[i] = array[length - i - 1];
        }

        return reversed;
    }

    static boolean matrixEquals(Integer[][] a, Integer[][] a2) {
        if (a==a2)
            return true;
        if (a==null || a2==null)
            return false;

        int length = a.length;
        if (a2.length != length)
            return false;

        for (int i=0; i<length; i++) {
            if (!Arrays.equals(a[i], a2[i]))
                return false;
        }

        return true;
    }

    static Integer[][] createAlignedBoard(Integer[] gameBoard, CardinalDirection direction) {
        Integer[][] aligned = toMatrix(gameBoard, Board.ROWS_COLS, Board.ROWS_COLS);

        if (direction.getAlignment() == Alignment.VERTICAL) {
            aligned = transpose(aligned);
        }

        if (direction.getDirection() == 1) {
            aligned = reverseRows(aligned);
        }

        return aligned;
    }

    static Integer[] createGameBoard(Integer[][] aligned, CardinalDirection direction) {
        if (direction.getDirection() == 1) {
            aligned = reverseRows(aligned);
        }

        if (direction.getAlignment() == Alignment.VERTICAL) {
            aligned = transpose(aligned);
        }

        return toArray(aligned);
    }

    static RowMoveInformation moveRow(Integer[] row) {
        int cols = row.length;
        Integer[] moved = Arrays.copyOf(row, cols);

        RowMoveInformation rowMoveInformation = new RowMoveInformation(row, moved);

        for (int col = 0; col < cols - 1; col++) {
            RowMoveLogic logic = RowMoveLogic.applicableLogic(moved[col], moved[col + 1]);
            if (logic != null) {
                rowMoveInformation.addAppliedLogic(logic);
                moved[col] = logic.move.apply(moved[col + 1]);
                moved[col + 1] = null;
            }
        }

        return rowMoveInformation;
    }

    void addNewCard(MoveInformation rowMoves) {
        Integer totalWeight = rowMoves.rowMoveInformation.stream()
                .map(RowMoveInformation::getCombinedNewCardWeight)
                .reduce(0, Math::addExact);

        Integer selector = random.nextInt(totalWeight);
        Integer accumulator = 0;

        for (RowMoveInformation rowMove : rowMoves.rowMoveInformation) {
            accumulator += rowMove.combinedNewCardWeight;
            if (accumulator > selector) {
                Integer[] row = rowMove.after;
                row[row.length - 1] = cardStack.pop();
                break;
            }
        }
    }

    static MoveInformation buildMoveInformation(Integer[] gameBoard, CardinalDirection direction) {
        Integer[][] aligned = createAlignedBoard(gameBoard, direction);

        return new MoveInformation(Arrays.stream(aligned)
                .map(PrimeMover::moveRow)
                .collect(Collectors.toList())
        );
    }

    public Integer[] move(Integer[] gameBoard, CardinalDirection direction) {
        MoveInformation moveInformation = buildMoveInformation(gameBoard, direction);

        addNewCard(moveInformation);

        Integer[][] movedBoard = new Integer[Board.ROWS_COLS][Board.ROWS_COLS];
        return createGameBoard(
                moveInformation.rowMoveInformation.stream()
                        .map(RowMoveInformation::getAfter)
                        .collect(Collectors.toList())
                        .toArray(movedBoard),
                direction
        );
    }

    static class MoveInformation {
        private final List<RowMoveInformation> rowMoveInformation;

        public MoveInformation(List<RowMoveInformation> rowMoveInformation) {
            this.rowMoveInformation = rowMoveInformation;
        }

        public List<RowMoveInformation> getRowMoveInformation() {
            return rowMoveInformation;
        }

        public boolean didMove() {
            return rowMoveInformation.stream()
                    .anyMatch(info -> info.appliedLogic.size() > 0);
        }
    }

    static class RowMoveInformation {
        private final List<RowMoveLogic> appliedLogic = new ArrayList<>();
        private Integer[] before;
        private Integer[] after;

        private Integer combinedNewCardWeight = 0;

        public RowMoveInformation(Integer[] before, Integer[] after) {
            this.before = before;
            this.after = after;
        }

        public void addAppliedLogic(RowMoveLogic logic) {
            combinedNewCardWeight += logic.newCardWeight;
            appliedLogic.add(logic);
        }

        public Integer getCombinedNewCardWeight() {
            return combinedNewCardWeight;
        }

        public boolean didMove() {
            return appliedLogic.size() > 0;
        }

        public Integer[] getBefore() {
            return before;
        }

        public Integer[] getAfter() {
            return after;
        }
    }

    private static class RowMoveLogic {
        private final BiFunction<Integer, Integer, Boolean> canMove;
        private final Function<Integer, Integer> move;
        private final Integer newCardWeight;

        private RowMoveLogic(
                BiFunction<Integer, Integer, Boolean> canMove,
                Function<Integer, Integer> move,
                Integer newCardWeight) {
            this.canMove = canMove;
            this.move = move;
            this.newCardWeight = newCardWeight;
        }

        public Function<Integer, Integer> getMove() {
            return move;
        }

        public Integer getNewCardWeight() {
            return newCardWeight;
        }

        private static final Map<String, RowMoveLogic> MOVE_LOGIC = new HashMap<>();
        static {
            MOVE_LOGIC.put("Moves to empty cell", new RowMoveLogic(
                    (thisCell, nextCell) -> thisCell == null && nextCell != null,
                    nextCell -> nextCell,
                    2
            ));
            MOVE_LOGIC.put("Combine cells with same value", new RowMoveLogic(
                    (thisCell, nextCell) -> thisCell != null && thisCell >= 0 && thisCell.equals(nextCell),
                    nextCell -> nextCell + 1,
                    3
            ));
            MOVE_LOGIC.put("1 and 2 combine to make 3", new RowMoveLogic(
                    (thisCell, nextCell) -> thisCell != null && nextCell != null && thisCell + nextCell == -3,
                    nextCell -> 0,
                    4
            ));
        }

        public static RowMoveLogic applicableLogic(Integer thisCell, Integer nextCell) {
            for (Map.Entry<String, RowMoveLogic> logic : MOVE_LOGIC.entrySet()) {
                if (!logic.getValue().canMove.apply(thisCell, nextCell)) {
                    continue;
                }

                return logic.getValue();
            }

            return null;
        }
    }
}
