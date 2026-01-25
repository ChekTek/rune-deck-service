package com.chektek.payload;

import net.runelite.api.Client;
import net.runelite.api.World;

public class WorldsPayload extends Payload {
    private World[] worlds;
    
    public WorldsPayload() {
        super(PayloadType.WORLDS);
    }

    public WorldsPayload(Client client) {
        super(PayloadType.WORLDS);
        this.worlds = client.getWorldList();
    }

    public World[] getWorlds() {
        return worlds;
    }

    @Override
    public boolean isNewPayload(Client client) {
        return true;
    }
}
