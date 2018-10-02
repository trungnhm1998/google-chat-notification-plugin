package io.cnaik.model.google;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.Arrays;

public final class Response implements Serializable {

    private final Cards[] cards;

    public Response(Cards[] cards) {
        this.cards = SerializationUtils.clone(cards);
    }

    public Cards[] getCards() {
        return SerializationUtils.clone(this.cards);
    }

    @Override
    public String toString() {
        return "Response{" +
                "cards=" + Arrays.toString(cards) +
                '}';
    }
}
