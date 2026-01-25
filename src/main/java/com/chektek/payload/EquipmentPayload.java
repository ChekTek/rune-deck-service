package com.chektek.payload;

import net.runelite.api.Client;
import net.runelite.api.PlayerComposition;

import java.util.Arrays;

public class EquipmentPayload extends Payload {
    private int[] playerEquipmentIds;

    public EquipmentPayload() {
        super(PayloadType.EQUIPMENT);
    }
    
    public EquipmentPayload(Client client) {
        super(PayloadType.EQUIPMENT);

        PlayerComposition composition = client.getLocalPlayer().getPlayerComposition();
        this.playerEquipmentIds = composition != null ? composition.getEquipmentIds() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentPayload that = (EquipmentPayload) o;
        return Arrays.equals(playerEquipmentIds, that.playerEquipmentIds);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(playerEquipmentIds);
    }

    public int[] getPlayerEquipmentIds() {
        return playerEquipmentIds;
    }

    @Override
    public boolean isNewPayload(Client client) {
        PlayerComposition composition = client.getLocalPlayer().getPlayerComposition();
        return composition == null ? this.getPlayerEquipmentIds() != null : !Arrays.equals(composition.getEquipmentIds(), this.getPlayerEquipmentIds());
    }
}
