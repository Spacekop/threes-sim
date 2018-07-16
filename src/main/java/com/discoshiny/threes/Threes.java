package com.discoshiny.threes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Threes {
    private static final Logger LOG = LoggerFactory.getLogger(Threes.class);

    private CardStack cardStack;
    private Board board;
    private Long seed;

    public Threes(Long seed) {
        this.seed = seed;
        reset();
    }

    public void reset() {
        this.cardStack = new CardStack(seed);
        this.board = new Board(
                seed,
                new PrimeMover(cardStack, seed),
                cardStack,
                createStartingBoard(cardStack, seed));
    }

    static Integer[] createStartingBoard(CardStack cardStack, Long seed) {
        List<Integer> positions = Arrays.stream(new Integer[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15})
                .collect(Collectors.toList());
        Collections.shuffle(positions, new Random(seed));

        Integer[] board = new Integer[16];
        for (int i = 0; i < 16; i++) {
            board[positions.get(i)] = i < 9 ? cardStack.pop() : null;
        }

        return board;
    }

    private static final Map<String, CardinalDirection> CONTROLS = new HashMap<>();
    static {
        CONTROLS.put("w", CardinalDirection.UP);
        CONTROLS.put("d", CardinalDirection.RIGHT);
        CONTROLS.put("s", CardinalDirection.DOWN);
        CONTROLS.put("a", CardinalDirection.LEFT);
    }

    private static String readLine() {
        Scanner s = new Scanner(System.in);
        return s.nextLine();
    }

    void play(List<CardinalDirection> replay) {
        if (replay.size() > 0) {
            LOG.info("Replaying saved moves. {} of them...", replay.size());
            int i = 1;
            for (CardinalDirection direction : replay) {
                board.move(direction);
            }
        }

        while (board.canMove()) {
            board.logBoard();
            String input = readLine();
            CardinalDirection direction = CONTROLS.get(input);
            if (direction != null) {
                board.move(direction);
            }
        }

        board.logBoard();
        LOG.info("Game over");
        LOG.info(board.saveGame().json());
    }

    void play() {
        play(new ArrayList<>());
    }

    private static SavedGame load(String fileName) throws Exception {
        File file = new File(fileName);

        return new ObjectMapper().readValue(file, SavedGame.class);
    }

    public static void main(String[] arg) throws Exception {
        Long seed = System.nanoTime();
        List<CardinalDirection> moves = new ArrayList<>();

        if (arg.length > 0) {
            SavedGame savedGame = load(arg[0]);
            seed = savedGame.getSeed();
            moves = savedGame.getMoves();
        }

        new Scanner(System.in).nextLine();

        Threes game = new Threes(seed);

        game.play(moves);
    }
}
