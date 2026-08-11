package com.crystalox.crate.economy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public final class EconomyBridge {

    private EconomyBridge() {
    }

    public static boolean deposit(Player player, double amount) {
        Plugin eco = Bukkit.getPluginManager().getPlugin("EconomyPlus");
        if (eco == null || !eco.isEnabled()) {
            return false;
        }
        try {
            Class<?> api = Class.forName("com.crystalox.economy.api.EconomyPlusAPI");
            String currency = (String) api.getMethod("getDefaultCurrency").invoke(null);
            Object result = api.getMethod("deposit", UUID.class, String.class, double.class)
                    .invoke(null, player.getUniqueId(), currency, amount);
            return Boolean.TRUE.equals(result);
        } catch (Throwable t) {
            return false;
        }
    }
}
