package com.chektek.payload;

import net.runelite.api.Client;

import java.util.Objects;

public class PVPPayload extends Payload {

    private Integer skullIcon;
    
    public PVPPayload() {
        super(PayloadType.PVP);
    }

    public PVPPayload(Client client) {
        super(PayloadType.PVP);
        int skullIcon = client.getLocalPlayer().getSkullIcon();
        this.skullIcon = skullIcon >= 0 ? skullIcon : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PVPPayload that = (PVPPayload) o;
        return Objects.equals(skullIcon, that.skullIcon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skullIcon);
    }

    public Integer getSkullIcon() {
        return skullIcon;
    }

    @Override
    public boolean isNewPayload(Client client) {
        int skullIcon = client.getLocalPlayer().getSkullIcon();
        Integer currentSkull = skullIcon >= 0 ? skullIcon : null;
        return !Objects.equals(currentSkull, this.skullIcon);
    }
}
