package com.crystalox.crate.util;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ItemParser {

    private ItemParser() {
    }

    public static ItemStack parse(ConfigurationSection s) {
        if (s == null) {
            return null;
        }
        ItemStack item = new ItemStack(material(s), s.getInt("amount", 1));
        ItemMeta meta = item.getItemMeta();
        applyName(meta, s);
        applyLore(meta, s);
        applyEnchants(meta, s);
        applyGlow(meta, s);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean matches(ItemStack item, ItemStack spec) {
        if (item == null || spec == null || item.getType() != spec.getType()) {
            return false;
        }
        return nameMatches(item, spec) && loreMatches(item, spec);
    }

    private static Material material(ConfigurationSection s) {
        String raw = s.getString("material", "STONE");
        Material material = Material.getMaterial(raw.toUpperCase());
        return material == null ? Material.STONE : material;
    }

    private static void applyName(ItemMeta meta, ConfigurationSection s) {
        if (s.contains("name")) {
            meta.setDisplayName(Message.color(s.getString("name")));
        }
    }

    private static void applyLore(ItemMeta meta, ConfigurationSection s) {
        if (s.contains("lore")) {
            List<String> lore = s.getStringList("lore");
            lore.replaceAll(Message::color);
            meta.setLore(lore);
        }
    }

    private static void applyEnchants(ItemMeta meta, ConfigurationSection s) {
        ConfigurationSection enchants = s.getConfigurationSection("enchants");
        if (enchants == null) {
            return;
        }
        for (String key : enchants.getKeys(false)) {
            Enchantment enchant = Enchantment.getByName(key);
            if (enchant != null) {
                meta.addEnchant(enchant, enchants.getInt(key), true);
            }
        }
    }

    private static void applyGlow(ItemMeta meta, ConfigurationSection s) {
        if (s.getBoolean("glow", false)) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
    }

    private static boolean nameMatches(ItemStack item, ItemStack spec) {
        if (!spec.hasItemMeta() || !spec.getItemMeta().hasDisplayName()) {
            return true;
        }
        return item.hasItemMeta()
                && item.getItemMeta().hasDisplayName()
                && item.getItemMeta().getDisplayName().equals(spec.getItemMeta().getDisplayName());
    }

    private static boolean loreMatches(ItemStack item, ItemStack spec) {
        if (!spec.hasItemMeta() || !spec.getItemMeta().hasLore()) {
            return true;
        }
        return item.hasItemMeta()
                && item.getItemMeta().hasLore()
                && item.getItemMeta().getLore().equals(spec.getItemMeta().getLore());
    }
}
