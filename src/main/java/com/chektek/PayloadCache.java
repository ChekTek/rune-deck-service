package com.chektek;

import com.chektek.payload.*;
import net.runelite.api.Client;
import net.runelite.api.HeadIcon;

import java.util.Objects;

public class PayloadCache {

	private static PayloadCache instance = new PayloadCache();

	public ActivityPayload activityPayload = new ActivityPayload();
	public FPSPayload fpsPayload = new FPSPayload();
	public EquipmentPayload equipmentPayload = new EquipmentPayload();
	public GrandExchangePayload grandExchangePayload = new GrandExchangePayload();
	public OverheadPayload overheadPayload = new OverheadPayload();
	public PVPPayload pvpPayload = new PVPPayload();
	public SkillsPayload skillsPayload = new SkillsPayload();
	public MovementPayload movementPayload = new MovementPayload();

	public static PayloadCache getInstance() {
		return instance;
	}

	public void clearCache() {
		activityPayload = new ActivityPayload();
		fpsPayload = new FPSPayload();
		equipmentPayload = new EquipmentPayload();
		grandExchangePayload = new GrandExchangePayload();
		overheadPayload = new OverheadPayload();
		pvpPayload = new PVPPayload();
		skillsPayload = new SkillsPayload();
		movementPayload = new MovementPayload();

	}

	public boolean isNewOverheadPayloadRequired(Client client) {
		if (overheadPayload == null) {
			return true;
		} else {
			HeadIcon overheadIcon = client.getLocalPlayer().getOverheadIcon();
			if (overheadIcon == null) {
				return false;
			}

			return !Objects.equals(overheadPayload.getOverheadIcon(), overheadIcon.name());
		}
	}
}
