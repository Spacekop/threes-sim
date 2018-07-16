package com.discoshiny.threes;

import com.discoshiny.threes.fixtures.GameBoards;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class BoardTest {
    private static final Logger LOG = LoggerFactory.getLogger(BoardTest.class);

    @Test
    public void board_happy() {
        LOG.info("board_happy");
        Board board = new Board(17L, new PrimeMover(new CardStack()), new CardStack(), GameBoards.GOOD_MIX);

        board.logBoard();
        board.move(CardinalDirection.LEFT);
        board.logBoard();
        LOG.info(board.moveHistory());
    }

    @Test
    public void board_cantMove() {
        LOG.info("board_cantMove");
        Board board = new Board(17L, new PrimeMover(new CardStack()), new CardStack(), GameBoards.NO_MOVES);
        board.logBoard();
        LOG.info(board.moveHistory());
        assertFalse(board.canMove());
    }

    @Test
    public void board_whatGives() {
        LOG.info("board_whatGives");
        Board board = new Board(17L,
                new PrimeMover(new CardStack(17L), 17L),
                new CardStack(17L),
                GameBoards.WHAT_GIVES);

        board.logBoard();
        assertTrue(board.canMove());
        board.move(CardinalDirection.DOWN);
        board.logBoard();
    }
}
