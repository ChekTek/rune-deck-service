package com.chektek.payload;

import com.chektek.AnimationCollections;

import net.runelite.api.Client;
import net.runelite.api.Player;

public class ActivityPayload extends Payload {
	private boolean isActive;
	private static final int INACTIVE_DEBOUNCE_TICKS = 3;
	private transient int inactiveStreakTicks = 0;

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
		Player player = client.getLocalPlayer();
		if (player == null) {
			return false; // Safety check
		}

		int animation = player.getAnimation();
		boolean isFishing = AnimationCollections.isFishingAnimation(animation);

		// Player is performing a non-idle animation
		if (animation != -1) {
			// For fishing, only count as active if still targeting the fishing spot
			if (isFishing) {
				return player.getInteracting() != null;
			}
			return true; // Any other animation = active
		}

		// Player is targeting/interacting with another actor
		if (player.isInteracting()) {
			return true;
		}

		// Player is moving (walking/running)
		if (player.getPoseAnimation() != player.getIdlePoseAnimation()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isNewPayload(Client client) {
		boolean activeNow = isCurrentlyActive(client);

		if (activeNow) {
			inactiveStreakTicks = 0;
			return !this.isActive; // became active -> report immediately
		}

		// inactive this tick
		inactiveStreakTicks++;

		// If we were active, require N consecutive inactive ticks before reporting
		// inactive.
		if (this.isActive && inactiveStreakTicks < INACTIVE_DEBOUNCE_TICKS) {
			return false;
		}

		return this.isActive; // became (debounced) inactive -> report
	}
}
