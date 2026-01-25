package com.chektek.payload;

import net.runelite.api.Client;

abstract public class Payload {
    private PayloadType type;

    protected Payload(PayloadType type) {
        this.type = type;
    }

    public PayloadType getType() {
        return type;
    }

    public abstract boolean isNewPayload(Client client);
}
