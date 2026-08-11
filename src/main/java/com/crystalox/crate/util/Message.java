package com.crystalox.crate.util;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public final class Message {

    private Message() {
    }

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String of(ConfigurationSection msgs, String key) {
        String raw = msgs.getString(key);
        if (raw == null) {
            return color("&c<missing:" + key + ">");
        }
        return color(raw);
    }
}
