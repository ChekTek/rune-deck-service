package com.chektek.payload;

import com.chektek.AnimationCollections;
import net.runelite.api.Client;
import net.runelite.api.Player;

public class ActivityPayload extends Payload {
    private static int inactiveTicks = 0;
    private boolean isActive;

    public ActivityPayload() {
        super(PayloadType.ACTIVITY);
    }
    
    public ActivityPayload(Client client) {
        super(PayloadType.ACTIVITY);
        this.isActive = ActivityPayload.inactiveTicks < 3 || client.getLocalPlayer().isInteracting();
    }

    public ActivityPayload(boolean isInteracting) {
        super(PayloadType.ACTIVITY);
        this.isActive = isInteracting;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public boolean isNewPayload(Client client) {
        Player player = client.getLocalPlayer();
        int animation = player.getAnimation();

        // Ignore fishing animations because they are handled by isInteracting()
        if (animation == -1 || AnimationCollections.isFishingAnimation(animation)) {
            ActivityPayload.inactiveTicks++;
        } else {
            ActivityPayload.inactiveTicks = 0;
        }

        return this.isActive != (ActivityPayload.inactiveTicks < 3 || client.getLocalPlayer().isInteracting());
    }
}

