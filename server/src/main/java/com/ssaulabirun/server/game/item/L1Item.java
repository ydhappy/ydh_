package com.ssaulabirun.server.game.item;

public class L1Item {
    public enum ItemType {
        WEAPON, ARMOR, ACCESSORY, CONSUMABLE, ETC
    }

    private final int itemId;
    private final String name;
    private final ItemType type;
    private final int attack;
    private final int defense;
    private final int weight;
    private final int price;
    private final String description;

    public L1Item(int itemId, String name, ItemType type, int attack, int defense,
                  int weight, int price, String description) {
        this.itemId = itemId;
        this.name = name;
        this.type = type;
        this.attack = attack;
        this.defense = defense;
        this.weight = weight;
        this.price = price;
        this.description = description;
    }

    public int getItemId() { return itemId; }
    public String getName() { return name; }
    public ItemType getType() { return type; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getWeight() { return weight; }
    public int getPrice() { return price; }
    public String getDescription() { return description; }
}
