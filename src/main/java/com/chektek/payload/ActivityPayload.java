package com.chektek.payload;

import net.runelite.api.Client;
import net.runelite.api.Player;

public class ActivityPayload extends Payload {
    private boolean isActive;

    public ActivityPayload() {
        super(PayloadType.ACTIVITY);
    }
    
    public ActivityPayload(Client client) {
        super(PayloadType.ACTIVITY);
     
        this.isActive = isCurrentlyActive(client);
    }

    public boolean isActive() {
        return isActive;
    }

    private static boolean isCurrentlyActive(Client client) {
		// Player is NOT idle if any of these are true:
        // 1. getAnimation() != -1: Player is performing an action (skilling, combat animation, etc.)
        // 2. isInteracting(): Player is targeting/interacting with another actor
        // 3. getPoseAnimation() != getIdlePoseAnimation(): Player is walking/running (not in idle pose)
        Player player = client.getLocalPlayer();
        return player.getAnimation() != -1 
                || player.isInteracting() 
                || player.getPoseAnimation() != player.getIdlePoseAnimation();
    }

    @Override
    public boolean isNewPayload(Client client) {
        return this.isActive != isCurrentlyActive(client);
    }
}

