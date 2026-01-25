package com.chektek.payload;

import net.runelite.api.Client;
import net.runelite.api.HeadIcon;

import java.util.Objects;

public class OverheadPayload extends Payload {

    private String overheadIcon;
    
    public OverheadPayload() {
        super(PayloadType.OVERHEAD);
    }

    public OverheadPayload(Client client) {
        super(PayloadType.OVERHEAD);

        HeadIcon overheadIcon = client.getLocalPlayer().getOverheadIcon();
        this.overheadIcon = overheadIcon != null ? overheadIcon.name() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OverheadPayload that = (OverheadPayload) o;
        return Objects.equals(overheadIcon, that.overheadIcon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(overheadIcon);
    }

    public String getOverheadIcon() {
        return overheadIcon;
    }

    @Override
    public boolean isNewPayload(Client client) {
        HeadIcon overheadIcon = client.getLocalPlayer().getOverheadIcon();
        return overheadIcon == null ? this.getOverheadIcon() != null : !overheadIcon.name().equals(this.getOverheadIcon());
    }
}
