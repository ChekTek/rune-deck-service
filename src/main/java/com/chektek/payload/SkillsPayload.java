package com.chektek.payload;

import net.runelite.api.Client;
import net.runelite.api.Skill;

import java.util.Objects;

public class SkillsPayload extends Payload {
    private int currentHealth;
    private int totalHealth;
    private int currentAttack;
    private int totalAttack;
    private int currentStrength;
    private int totalStrength;
    private int currentDefence;
    private int totalDefence;
    private int currentRange;
    private int totalRange;
    private int currentPrayer;
    private int totalPrayer;
    private int currentMagic;
    private int totalMagic;
    private int currentRunecrafting;
    private int totalRunecrafting;
    private int currentConstruction;
    private int totalConstruction;
    private int currentAgility;
    private int totalAgility;
    private int currentHerblore;
    private int totalHerblore;
    private int currentThieving;
    private int totalThieving;
    private int currentCrafting;
    private int totalCrafting;
    private int currentFletching;
    private int totalFletching;
    private int currentSlayer;
    private int totalSlayer;
    private int currentHunter;
    private int totalHunter;
    private int currentMining;
    private int totalMining;
    private int currentSmithing;
    private int totalSmithing;
    private int currentFishing;
    private int totalFishing;
    private int currentCooking;
    private int totalCooking;
    private int currentFiremaking;
    private int totalFiremaking;
    private int currentWoodcutting;
    private int totalWoodcutting;
    private int currentFarming;
    private int totalFarming;
    private int combatLevel;
    
    public SkillsPayload() {
        super(PayloadType.SKILLS);
    }

    public SkillsPayload(Client client) {
        super(PayloadType.SKILLS);

        this.currentHealth = client.getBoostedSkillLevel(Skill.HITPOINTS);
        this.totalHealth = client.getRealSkillLevel(Skill.HITPOINTS);
        this.currentAttack = client.getBoostedSkillLevel(Skill.ATTACK);
        this.totalAttack = client.getRealSkillLevel(Skill.ATTACK);
        this.currentStrength = client.getBoostedSkillLevel(Skill.STRENGTH);
        this.totalStrength = client.getRealSkillLevel(Skill.STRENGTH);
        this.currentDefence = client.getBoostedSkillLevel(Skill.DEFENCE);
        this.totalDefence = client.getRealSkillLevel(Skill.DEFENCE);
        this.currentRange = client.getBoostedSkillLevel(Skill.RANGED);
        this.totalRange = client.getRealSkillLevel(Skill.RANGED);
        this.currentPrayer = client.getBoostedSkillLevel(Skill.PRAYER);
        this.totalPrayer = client.getRealSkillLevel(Skill.PRAYER);
        this.currentMagic = client.getBoostedSkillLevel(Skill.MAGIC);
        this.totalMagic = client.getRealSkillLevel(Skill.MAGIC);
        this.currentRunecrafting = client.getBoostedSkillLevel(Skill.RUNECRAFT);
        this.totalRunecrafting = client.getRealSkillLevel(Skill.RUNECRAFT);
        this.currentConstruction = client.getBoostedSkillLevel(Skill.CONSTRUCTION);
        this.totalConstruction = client.getRealSkillLevel(Skill.CONSTRUCTION);
        this.currentAgility = client.getBoostedSkillLevel(Skill.AGILITY);
        this.totalAgility = client.getRealSkillLevel(Skill.AGILITY);
        this.currentHerblore = client.getBoostedSkillLevel(Skill.HERBLORE);
        this.totalHerblore = client.getRealSkillLevel(Skill.HERBLORE);
        this.currentThieving = client.getBoostedSkillLevel(Skill.THIEVING);
        this.totalThieving = client.getRealSkillLevel(Skill.THIEVING);
        this.currentCrafting = client.getBoostedSkillLevel(Skill.CRAFTING);
        this.totalCrafting = client.getRealSkillLevel(Skill.CRAFTING);
        this.currentFletching = client.getBoostedSkillLevel(Skill.FLETCHING);
        this.totalFletching = client.getRealSkillLevel(Skill.FLETCHING);
        this.currentSlayer = client.getBoostedSkillLevel(Skill.SLAYER);
        this.totalSlayer = client.getRealSkillLevel(Skill.SLAYER);
        this.currentHunter = client.getBoostedSkillLevel(Skill.HUNTER);
        this.totalHunter = client.getRealSkillLevel(Skill.HUNTER);
        this.currentMining = client.getBoostedSkillLevel(Skill.MINING);
        this.totalMining = client.getRealSkillLevel(Skill.MINING);
        this.currentSmithing = client.getBoostedSkillLevel(Skill.SMITHING);
        this.totalSmithing = client.getRealSkillLevel(Skill.SMITHING);
        this.currentFishing = client.getBoostedSkillLevel(Skill.FISHING);
        this.totalFishing = client.getRealSkillLevel(Skill.FISHING);
        this.currentCooking = client.getBoostedSkillLevel(Skill.COOKING);
        this.totalCooking = client.getRealSkillLevel(Skill.COOKING);
        this.currentFiremaking = client.getBoostedSkillLevel(Skill.FIREMAKING);
        this.totalFiremaking = client.getRealSkillLevel(Skill.FIREMAKING);
        this.currentWoodcutting = client.getBoostedSkillLevel(Skill.WOODCUTTING);
        this.totalWoodcutting = client.getRealSkillLevel(Skill.WOODCUTTING);
        this.currentFarming = client.getBoostedSkillLevel(Skill.FARMING);
        this.totalFarming = client.getRealSkillLevel(Skill.FARMING);

        this.combatLevel = client.getLocalPlayer().getCombatLevel();
    }

    public int getCurrentHealth() {
        return this.currentHealth;
    }

    public int getTotalHealth() {
        return this.totalHealth;
    }

    public int getCurrentAttack() {
        return this.currentAttack;
    }

    public int getTotalAttack() {
        return this.totalAttack;
    }

    public int getCurrentStrength() {
        return this.currentStrength;
    }

    public int getTotalStrength() {
        return this.totalStrength;
    }

    public int getCurrentDefence() {
        return this.currentDefence;
    }

    public int getTotalDefence() {
        return this.totalDefence;
    }

    public int getCurrentRange() {
        return this.currentRange;
    }

    public int getTotalRange() {
        return this.totalRange;
    }

    public int getCurrentPrayer() {
        return this.currentPrayer;
    }

    public int getTotalPrayer() {
        return this.totalPrayer;
    }

    public int getCurrentMagic() {
        return this.currentMagic;
    }

    public int getTotalMagic() {
        return this.totalMagic;
    }

    public int getCurrentRunecrafting() {
        return this.currentRunecrafting;
    }

    public int getTotalRunecrafting() {
        return this.totalRunecrafting;
    }

    public int getCurrentConstruction() {
        return this.currentConstruction;
    }

    public int getTotalConstruction() {
        return this.totalConstruction;
    }

    public int getCurrentAgility() {
        return this.currentAgility;
    }

    public int getTotalAgility() {
        return this.totalAgility;
    }

    public int getCurrentHerblore() {
        return this.currentHerblore;
    }

    public int getTotalHerblore() {
        return this.totalHerblore;
    }

    public int getCurrentThieving() {
        return this.currentThieving;
    }

    public int getTotalThieving() {
        return this.totalThieving;
    }

    public int getCurrentCrafting() {
        return this.currentCrafting;
    }

    public int getTotalCrafting() {
        return this.totalCrafting;
    }

    public int getCurrentFletching() {
        return this.currentFletching;
    }

    public int getTotalFletching() {
        return this.totalFletching;
    }

    public int getCurrentSlayer() {
        return this.currentSlayer;
    }

    public int getTotalSlayer() {
        return this.totalSlayer;
    }

    public int getCurrentHunter() {
        return this.currentHunter;
    }

    public int getTotalHunter() {
        return this.totalHunter;
    }

    public int getCurrentMining() {
        return this.currentMining;
    }

    public int getTotalMining() {
        return this.totalMining;
    }

    public int getCurrentSmithing() {
        return this.currentSmithing;
    }

    public int getTotalSmithing() {
        return this.totalSmithing;
    }

    public int getCurrentFishing() {
        return this.currentFishing;
    }

    public int getTotalFishing() {
        return this.totalFishing;
    }

    public int getCurrentCooking() {
        return this.currentCooking;
    }

    public int getTotalCooking() {
        return this.totalCooking;
    }

    public int getCurrentFiremaking() {
        return this.currentFiremaking;
    }

    public int getTotalFiremaking() {
        return this.totalFiremaking;
    }

    public int getCurrentWoodcutting() {
        return this.currentWoodcutting;
    }

    public int getTotalWoodcutting() {
        return this.totalWoodcutting;
    }

    public int getCurrentFarming() {
        return this.currentFarming;
    }

    public int getTotalFarming() {
        return this.totalFarming;
    }

    public int getCombatLevel() {
        return this.combatLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillsPayload that = (SkillsPayload) o;
        return currentHealth == that.currentHealth && totalHealth == that.totalHealth && currentAttack == that.currentAttack && totalAttack == that.totalAttack && currentStrength == that.currentStrength && totalStrength == that.totalStrength && currentDefence == that.currentDefence && totalDefence == that.totalDefence && currentRange == that.currentRange && totalRange == that.totalRange && currentPrayer == that.currentPrayer && totalPrayer == that.totalPrayer && currentMagic == that.currentMagic && totalMagic == that.totalMagic && currentRunecrafting == that.currentRunecrafting && totalRunecrafting == that.totalRunecrafting && currentConstruction == that.currentConstruction && totalConstruction == that.totalConstruction && currentAgility == that.currentAgility && totalAgility == that.totalAgility && currentHerblore == that.currentHerblore && totalHerblore == that.totalHerblore && currentThieving == that.currentThieving && totalThieving == that.totalThieving && currentCrafting == that.currentCrafting && totalCrafting == that.totalCrafting && currentFletching == that.currentFletching && totalFletching == that.totalFletching && currentSlayer == that.currentSlayer && totalSlayer == that.totalSlayer && currentHunter == that.currentHunter && totalHunter == that.totalHunter && currentMining == that.currentMining && totalMining == that.totalMining && currentSmithing == that.currentSmithing && totalSmithing == that.totalSmithing && currentFishing == that.currentFishing && totalFishing == that.totalFishing && currentCooking == that.currentCooking && totalCooking == that.totalCooking && currentFiremaking == that.currentFiremaking && totalFiremaking == that.totalFiremaking && currentWoodcutting == that.currentWoodcutting && totalWoodcutting == that.totalWoodcutting && currentFarming == that.currentFarming && totalFarming == that.totalFarming && combatLevel == that.combatLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentHealth, totalHealth, currentAttack, totalAttack, currentStrength, totalStrength, currentDefence, totalDefence, currentRange, totalRange, currentPrayer, totalPrayer, currentMagic, totalMagic, currentRunecrafting, totalRunecrafting, currentConstruction, totalConstruction, currentAgility, totalAgility, currentHerblore, totalHerblore, currentThieving, totalThieving, currentCrafting, totalCrafting, currentFletching, totalFletching, currentSlayer, totalSlayer, currentHunter, totalHunter, currentMining, totalMining, currentSmithing, totalSmithing, currentFishing, totalFishing, currentCooking, totalCooking, currentFiremaking, totalFiremaking, currentWoodcutting, totalWoodcutting, currentFarming, totalFarming, combatLevel);
    }

    @Override
    public boolean isNewPayload(Client client) {
        return this.currentHealth != client.getBoostedSkillLevel(Skill.HITPOINTS) ||
                this.totalHealth != client.getRealSkillLevel(Skill.HITPOINTS) ||
                this.currentAttack != client.getBoostedSkillLevel(Skill.ATTACK) ||
                this.totalAttack != client.getRealSkillLevel(Skill.ATTACK) ||
                this.currentStrength != client.getBoostedSkillLevel(Skill.STRENGTH) ||
                this.totalStrength != client.getRealSkillLevel(Skill.STRENGTH) ||
                this.currentDefence != client.getBoostedSkillLevel(Skill.DEFENCE) ||
                this.totalDefence != client.getRealSkillLevel(Skill.DEFENCE) ||
                this.currentRange != client.getBoostedSkillLevel(Skill.RANGED) ||
                this.totalRange != client.getRealSkillLevel(Skill.RANGED) ||
                this.currentPrayer != client.getBoostedSkillLevel(Skill.PRAYER) ||
                this.totalPrayer != client.getRealSkillLevel(Skill.PRAYER) ||
                this.currentMagic != client.getBoostedSkillLevel(Skill.MAGIC) ||
                this.totalMagic != client.getRealSkillLevel(Skill.MAGIC) ||
                this.currentRunecrafting != client.getBoostedSkillLevel(Skill.RUNECRAFT) ||
                this.totalRunecrafting != client.getRealSkillLevel(Skill.RUNECRAFT) ||
                this.currentConstruction != client.getBoostedSkillLevel(Skill.CONSTRUCTION) ||
                this.totalConstruction != client.getRealSkillLevel(Skill.CONSTRUCTION) ||
                this.currentAgility != client.getBoostedSkillLevel(Skill.AGILITY) ||
                this.totalAgility != client.getRealSkillLevel(Skill.AGILITY) ||
                this.currentHerblore != client.getBoostedSkillLevel(Skill.HERBLORE) ||
                this.totalHerblore != client.getRealSkillLevel(Skill.HERBLORE) ||
                this.currentThieving != client.getBoostedSkillLevel(Skill.THIEVING) ||
                this.totalThieving != client.getRealSkillLevel(Skill.THIEVING) ||
                this.currentCrafting != client.getBoostedSkillLevel(Skill.CRAFTING) ||
                this.totalCrafting != client.getRealSkillLevel(Skill.CRAFTING) ||
                this.currentFletching != client.getBoostedSkillLevel(Skill.FLETCHING) ||
                this.combatLevel != client.getLocalPlayer().getCombatLevel();
    }
}
