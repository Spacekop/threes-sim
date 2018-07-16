package com.discoshiny.threes;

import java.util.Stack;

public class TranslatedCardStack extends Stack<Card> {
    private final Stack<Integer> cardStack;

    public TranslatedCardStack(Stack<Integer> cardStack) {
        this.cardStack = cardStack;
    }

    @Override
    public Card push(Card item) {
        throw new UnsupportedOperationException("no pushing");
    }

    @Override
    public synchronized Card pop() {
        return new Card(cardStack.pop());
    }

    @Override
    public synchronized Card peek() {
        return new Card(cardStack.peek());
    }

    @Override
    public boolean empty() {
        return cardStack.empty();
    }

    @Override
    public synchronized int search(Object o) {
        throw new UnsupportedOperationException("no searching");
    }
}
