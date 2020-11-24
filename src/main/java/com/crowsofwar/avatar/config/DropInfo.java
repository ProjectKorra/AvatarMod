package com.crowsofwar.avatar.config;

import com.crowsofwar.avatar.item.scroll.Scrolls;

public class DropInfo {

    private int tier;
    private Scrolls.ScrollType type;
    private double dropChance;
    private int amount;

    public DropInfo(Scrolls.ScrollType type, int tier, double chance) {
        this.type = type;
        this.tier = tier;
        this.dropChance = chance;
        this.amount = 1;
    }

    public DropInfo(Scrolls.ScrollType type, int tier, double chance, int amount) {
        this.type = type;
        this.tier = tier;
        this.dropChance = chance;
        this.amount = amount;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public int getTier() {
        return this.tier;
    }

    public void setType(Scrolls.ScrollType type) {
        this.type = type;
    }

    public Scrolls.ScrollType getType() {
        return this.type;
    }

    public void setDropChance(double chance) {
        this.dropChance = chance;
    }

    public double getDropChance() {
        return this.dropChance;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
    public int getAmount() {
        return this.amount;
    }
}
