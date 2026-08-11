package com.crystalox.crate.reward;

import com.crystalox.crate.crate.CrateManager;
import com.crystalox.crate.crate.Reward;
import com.crystalox.crate.economy.EconomyBridge;
import com.crystalox.crate.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public final class RewardHandler {

    private RewardHandler() {
    }

    public static void give(Player player, Reward reward, CrateManager crateManager, JavaPlugin plugin) {
        switch (reward.getType()) {
            case ITEM:
                giveItem(player, reward, crateManager);
                break;
            case MONEY:
                giveMoney(player, reward, crateManager);
                break;
            case COMMAND:
                giveCommand(player, reward, crateManager);
                break;
        }
    }

    private static void giveItem(Player player, Reward reward, CrateManager crateManager) {
        ItemStack item = reward.getItem();
        if (item != null) {
            Map<Integer, ItemStack> leftover = player.getInventory().addItem(item.clone());
            for (ItemStack drop : leftover.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), drop);
            }
        }
        player.sendMessage(msg(crateManager, "won-item").replace("%item%", rewardName(reward)));
    }

    private static void giveMoney(Player player, Reward reward, CrateManager crateManager) {
        if (EconomyBridge.deposit(player, reward.getAmount())) {
            player.sendMessage(msg(crateManager, "won-money").replace("%amount%", String.valueOf(reward.getAmount())));
            return;
        }
        player.sendMessage(msg(crateManager, "economy-missing"));
    }

    private static void giveCommand(Player player, Reward reward, CrateManager crateManager) {
        String command = reward.getCommand();
        if (command == null) {
            return;
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
        player.sendMessage(msg(crateManager, "won-command"));
    }

    private static String rewardName(Reward reward) {
        switch (reward.getType()) {
            case ITEM:
                return itemName(reward);
            case MONEY:
                return "Coins x" + reward.getAmount();
            default:
                return "Command reward";
        }
    }

    private static String itemName(Reward reward) {
        ItemStack item = reward.getItem();
        if (item == null) {
            return "Item";
        }
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }
        return item.getType().name();
    }

    private static String msg(CrateManager crateManager, String key) {
        return Message.of(crateManager.getMessages(), key);
    }
}
