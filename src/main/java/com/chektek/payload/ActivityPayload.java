package com.chektek.payload;

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
		// Player is NOT idle if any of these are true:
		// 1. getAnimation() != -1: Player is performing an action (skilling, combat
		// animation, etc.)
		// 2. isInteracting(): Player is targeting/interacting with another actor
		// 3. getPoseAnimation() != getIdlePoseAnimation(): Player is walking/running
		// (not in idle pose)
		Player player = client.getLocalPlayer();
		return player.getAnimation() != -1
				|| player.isInteracting()
				|| player.getPoseAnimation() != player.getIdlePoseAnimation();
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
