package com.discoshiny.threes;

import com.discoshiny.threes.fixtures.GameBoards;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.discoshiny.threes.fixtures.PureMatrix.ALIGNED_BOARD;
import static com.discoshiny.threes.fixtures.PureMatrix.GAME_BOARD;
import static org.junit.Assert.assertTrue;

public class PrimeMoverTest {
    private static final Logger LOG = LoggerFactory.getLogger(PrimeMoverTest.class);

    @Test
    public void toMatrix_happy() {
        Integer[][] matrix = PrimeMover.toMatrix(GAME_BOARD, 4, 4);

        LOG.info("{}", matrixToString(ALIGNED_BOARD));
        LOG.info("{}", matrixToString(matrix));

        assertTrue(PrimeMover.matrixEquals(ALIGNED_BOARD, matrix));
    }

    @Test
    public void toArray_happy() {
        Integer[] array = PrimeMover.toArray(ALIGNED_BOARD);

        LOG.info("{}", Arrays.toString(GAME_BOARD));
        LOG.info("{}", Arrays.toString(array));

        assertTrue(Arrays.equals(GAME_BOARD, array));
    }

    @Test
    public void transpose() {
        LOG.info("transposed: {}", matrixToString(PrimeMover.transpose(ALIGNED_BOARD)));
    }

    @Test
    public void reverseRows() {
        LOG.info("reversedRows: {}", matrixToString(PrimeMover.reverseRows(ALIGNED_BOARD)));
    }

    @Test
    public void move() {
        PrimeMover mover = new PrimeMover(new CardStack());
        mover.move(GameBoards.GOOD_MIX, CardinalDirection.UP);
        mover.move(GameBoards.GOOD_MIX, CardinalDirection.RIGHT);
        mover.move(GameBoards.GOOD_MIX, CardinalDirection.DOWN);
        mover.move(GameBoards.GOOD_MIX, CardinalDirection.LEFT);
    }

    static String matrixToString(Integer[][] matrix) {
        List<String> arrayStrings = Arrays.stream(matrix)
                .map(Arrays::toString)
                .collect(Collectors.toList());

        return String.format("[%s]", String.join(", ", arrayStrings));
    }
}
