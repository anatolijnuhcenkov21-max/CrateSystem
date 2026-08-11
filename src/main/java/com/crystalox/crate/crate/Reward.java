package com.crystalox.crate.crate;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import com.crystalox.crate.util.ItemParser;

public class Reward {

    public enum Type {
        ITEM,
        COMMAND,
        MONEY
    }

    private final Type type;
    private final int weight;
    private final ItemStack item;
    private final String command;
    private final double amount;

    private Reward(Type type, int weight, ItemStack item, String command, double amount) {
        this.type = type;
        this.weight = weight;
        this.item = item;
        this.command = command;
        this.amount = amount;
    }

    public static Reward fromConfig(ConfigurationSection s) {
        return new Reward(
                typeOf(s.getString("type", "ITEM")),
                s.getInt("weight", 1),
                ItemParser.parse(s.getConfigurationSection("item")),
                s.getString("command"),
                s.getDouble("amount", 0.0));
    }

    private static Type typeOf(String raw) {
        try {
            return Type.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Type.ITEM;
        }
    }

    public Type getType() {
        return type;
    }

    public int getWeight() {
        return weight;
    }

    public ItemStack getItem() {
        return item;
    }

    public String getCommand() {
        return command;
    }

    public double getAmount() {
        return amount;
    }
}
