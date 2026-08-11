package com.crystalox.crate.crate;

import java.util.List;
import java.util.Random;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class RewardRoller {

    private RewardRoller() {
    }

    public static Reward roll(List<Reward> rewards, Random random) {
        if (rewards == null || rewards.isEmpty()) {
            return null;
        }
        int totalWeight = 0;
        for (Reward reward : rewards) {
            totalWeight += reward.getWeight();
        }
        if (totalWeight <= 0) {
            return rewards.get(0);
        }
        int pick = random.nextInt(totalWeight);
        for (Reward reward : rewards) {
            pick -= reward.getWeight();
            if (pick < 0) {
                return reward;
            }
        }
        return rewards.get(rewards.size() - 1);
    }

    public static String rewardName(Reward reward) {
        switch (reward.getType()) {
            case ITEM:
                return itemName(reward.getItem());
            case MONEY:
                return "Coins x" + reward.getAmount();
            case COMMAND:
                return "Command reward";
            default:
                return "Reward";
        }
    }

    private static String itemName(ItemStack item) {
        if (item == null) {
            return "Item";
        }
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return meta.getDisplayName();
        }
        return item.getType().name();
    }
}
