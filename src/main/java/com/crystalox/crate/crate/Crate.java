package com.crystalox.crate.crate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.crystalox.crate.util.ItemParser;

public class Crate {

    private final String id;
    private final String displayName;
    private final ItemStack keyItem;
    private final List<Reward> rewards;

    public Crate(String id, String displayName, ItemStack keyItem, List<Reward> rewards) {
        this.id = id;
        this.displayName = displayName;
        this.keyItem = keyItem;
        this.rewards = rewards;
    }

    public static Crate fromConfig(String id, ConfigurationSection s) {
        String displayName = s.getString("display-name", id);
        ItemStack keyItem = ItemParser.parse(s.getConfigurationSection("key"));
        List<Reward> rewards = new ArrayList<Reward>();
        ConfigurationSection rewardsSection = s.getConfigurationSection("rewards");
        if (rewardsSection != null) {
            for (String key : rewardsSection.getKeys(false)) {
                rewards.add(Reward.fromConfig(rewardsSection.getConfigurationSection(key)));
            }
        } else {
            for (Object raw : s.getMapList("rewards")) {
                if (raw instanceof Map) {
                    rewards.add(Reward.fromConfig(sectionOf((Map<?, ?>) raw)));
                }
            }
        }
        return new Crate(id, displayName, keyItem, rewards);
    }

    private static ConfigurationSection sectionOf(Map<?, ?> map) {
        YamlConfiguration cfg = new YamlConfiguration();
        populate(cfg, map);
        return cfg;
    }

    private static void populate(ConfigurationSection target, Map<?, ?> map) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = String.valueOf(entry.getKey());
            Object value = entry.getValue();
            if (value instanceof Map) {
                populate(target.createSection(key), (Map<?, ?>) value);
            } else {
                target.set(key, value);
            }
        }
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ItemStack getKeyItem() {
        return keyItem;
    }

    public List<Reward> getRewards() {
        return Collections.unmodifiableList(rewards);
    }
}
