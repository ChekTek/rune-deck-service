package com.chektek.payload;

import net.runelite.api.Client;

public class LogoutPayload extends Payload {
	
    public LogoutPayload() {
        super(PayloadType.LOGOUT);
    }

    @Override
    public boolean isNewPayload(Client client) {
        return true;
    }
}
