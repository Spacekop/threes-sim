package com.discoshiny.threes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class SavedGame {
    @JsonProperty
    private Long seed;

    @JsonProperty
    private List<CardinalDirection> moves;

    public SavedGame() {
    }

    public SavedGame(Long seed, List<CardinalDirection> moves) {
        this.seed = seed;
        this.moves = moves;
    }

    public Long getSeed() {
        return seed;
    }

    public List<CardinalDirection> getMoves() {
        return moves;
    }

    @JsonIgnore
    String json() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
