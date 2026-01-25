package com.chektek.payload;

import net.runelite.api.Client;
import net.runelite.api.GrandExchangeOffer;

import java.util.Arrays;
import java.util.Objects;

public class GrandExchangePayload extends Payload {
    private Offer[] offers;

    public GrandExchangePayload() {
        super(PayloadType.GRAND_EXCHANGE);
    }
    
    public GrandExchangePayload(Client client) {
        super(PayloadType.GRAND_EXCHANGE);

        GrandExchangeOffer[] grandExchangeOffers = client.getGrandExchangeOffers();

        offers = new Offer[grandExchangeOffers.length];

        for (int i = 0; i < offers.length; i++) {
            offers[i] = new Offer(grandExchangeOffers[i]);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GrandExchangePayload that = (GrandExchangePayload) o;
        return Arrays.equals(offers, that.offers);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(offers);
    }

    public Offer[] getOffers() {
        return offers;
    }

    @Override
    public boolean isNewPayload(Client client) {
        GrandExchangeOffer[] grandExchangeOffers = client.getGrandExchangeOffers();

        Offer[] newOffers = new Offer[grandExchangeOffers.length];

        for (int i = 0; i < newOffers.length; i++) {
            newOffers[i] = new Offer(grandExchangeOffers[i]);
        }

        return !Arrays.equals(this.getOffers(), newOffers);
    }
}

class Offer {
    private final int itemId;
    private final int price;
    private final int quantitySold;
    private final int spent;
    private final int totalQuantity;
    private final String state;

    Offer(GrandExchangeOffer offer) {
        this.itemId = offer.getItemId();
        this.price = offer.getPrice();
        this.quantitySold = offer.getQuantitySold();
        this.spent = offer.getSpent();
        this.totalQuantity = offer.getTotalQuantity();
        this.state = offer.getState().name();
    }

    public int getItemId() {
        return itemId;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public int getSpent() {
        return spent;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public String getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Offer offer = (Offer) o;
        return itemId == offer.itemId && price == offer.price && quantitySold == offer.quantitySold && spent == offer.spent && totalQuantity == offer.totalQuantity && Objects.equals(state, offer.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, price, quantitySold, spent, totalQuantity, state);
    }
}
