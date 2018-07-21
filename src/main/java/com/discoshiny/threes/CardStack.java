package com.discoshiny.threes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;
import java.util.stream.Collectors;

public class CardStack extends Stack<Integer> {
    private static final Logger LOG = LoggerFactory.getLogger(CardStack.class);
    private static final int[] STACK_CARDS = {
            Card.ONE_CARD_RANK,
            Card.ONE_CARD_RANK,
            Card.ONE_CARD_RANK,
            Card.ONE_CARD_RANK,

            Card.TWO_CARD_RANK,
            Card.TWO_CARD_RANK,
            Card.TWO_CARD_RANK,
            Card.TWO_CARD_RANK,

            0, 0, 0, 0
    };


    private final Random random;
    private final Queue<Integer> queue;

    private Integer largestRank = 0;
    private Integer[] possibleWildRanks = {};
    private Integer wildRank = 0;
    private Integer drawsSinceLastWild = 0;
    private static final Integer MAX_DRAWS_BETWEEN_WILD = 15;
    private static final Integer MIN_DRAWS_BETWEEN_WILD = 9;

    public CardStack(Long seed) {
        queue = new ArrayDeque<>();
        random = Optional.ofNullable(seed)
                .map(Random::new)
                .orElse(new Random());

        fillStack();
    }

    public CardStack() {
        this(null);
    }

    void fillStack() {
        List<Integer> cards = Arrays.stream(Arrays.copyOf(STACK_CARDS, STACK_CARDS.length)).boxed()
                .collect(Collectors.toList());
        Collections.shuffle(cards, random);

        cards.forEach(queue::offer);
    }

    @Override
    public Integer push(Integer item) {
        throw new UnsupportedOperationException("no pushing");
    }

    @Override
    public synchronized Integer pop() {
        if (wildRank > 0) {
            Integer popped = wildRank;

            wildRank = 0;
            drawsSinceLastWild = 0;

            return popped;
        }

        Integer item = queue.remove();
        drawsSinceLastWild++;
        LOG.info("Draws since last wild: {}", drawsSinceLastWild);

        if (queue.isEmpty()) {
            fillStack();
        }

        Integer wildDraw = random.nextInt(
                MAX_DRAWS_BETWEEN_WILD - MIN_DRAWS_BETWEEN_WILD) +
                MIN_DRAWS_BETWEEN_WILD;

        LOG.info("Wild draw! Got {}", wildDraw);

        if (drawsSinceLastWild > wildDraw) {
            LOG.info("GO WILD");
            if (possibleWildRanks.length == 0) {
                // Misfire!
                drawsSinceLastWild = 0;
            } else if (possibleWildRanks.length == 1) {
                wildRank = possibleWildRanks[0];
            } else {
                wildRank = possibleWildRanks[random.nextInt(possibleWildRanks.length)];
            }
        }

        return item;
    }

    @Override
    public synchronized Integer peek() {
        if (wildRank > 0) {
            return Card.WILD_RANK;
        }

        return queue.peek();
    }

    @Override
    public boolean empty() {
        return queue.isEmpty();
    }

    @Override
    public synchronized int search(Object o) {
        throw new UnsupportedOperationException("no searching!!!");
    }

    public void setLargestRank(Integer largestRank) {
        this.largestRank = largestRank;
        this.possibleWildRanks = getPossibleWildRanks();

        LOG.info("New largest rank: {}", this.largestRank);
        LOG.info("New possible wildcards: {}", Arrays.toString(possibleWildRanks));
    }

    private Integer[] getPossibleWildRanks() {
        List<Integer> possibleWildRanks = new ArrayList<>();

        for (int i = 1; i < largestRank - 2; i++) {
            possibleWildRanks.add(i);
        }

        Integer[] possibleWildRanksArray = new Integer[possibleWildRanks.size()];
        if (possibleWildRanks.size() == 0) {
            return possibleWildRanksArray;
        }

        return possibleWildRanks.toArray(possibleWildRanksArray);
    }
}
