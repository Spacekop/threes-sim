package com.discoshiny.threes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
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

    private final Random random;

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

    private final Queue<Integer> queue;

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
        Integer item = queue.remove();

        if (queue.isEmpty()) {
            fillStack();
        }

        return item;
    }

    @Override
    public synchronized Integer peek() {
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
}
