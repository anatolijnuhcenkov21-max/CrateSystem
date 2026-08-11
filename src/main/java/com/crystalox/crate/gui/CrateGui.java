package com.crystalox.crate.gui;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.crystalox.crate.crate.Crate;
import com.crystalox.crate.crate.CrateManager;
import com.crystalox.crate.crate.Reward;
import com.crystalox.crate.crate.RewardRoller;
import com.crystalox.crate.reward.RewardHandler;
import com.crystalox.crate.util.Message;

public final class CrateGui {

    private static final Set<UUID> OPENING = ConcurrentHashMap.newKeySet();
    private static final Map<UUID, BukkitTask> ANIM_TASKS = new ConcurrentHashMap<UUID, BukkitTask>();
    private static final Map<UUID, BukkitTask> GRANT_TASKS = new ConcurrentHashMap<UUID, BukkitTask>();

    private CrateGui() {
    }

    public static void open(Player player, Crate crate, JavaPlugin plugin, CrateManager crateManager) {
        if (!OPENING.add(player.getUniqueId())) {
            return;
        }
        String title = crateManager.getMessages().getString("gui-title", "&8Crate").replace("%crate%", crate.getDisplayName());
        Inventory inv = Bukkit.createInventory(null, 27, Message.color(title));
        player.openInventory(inv);
        final Random random = new Random();
        AtomicInteger cycles = new AtomicInteger(0);
        final AtomicReference<BukkitTask> taskRef = new AtomicReference<BukkitTask>();
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            Reward filler = RewardRoller.roll(crate.getRewards(), random);
            ItemStack icon = filler.getItem() != null ? filler.getItem().clone() : buildFallback(filler);
            for (int i = 0; i < 27; i++) {
                inv.setItem(i, icon);
            }
            if (cycles.incrementAndGet() >= crateManager.getAnimationCycles()) {
                taskRef.get().cancel();
                ANIM_TASKS.remove(player.getUniqueId());
                Reward finalReward = RewardRoller.roll(crate.getRewards(), random);
                ItemStack finalIcon = finalReward.getItem() != null ? finalReward.getItem().clone() : buildFallback(finalReward);
                for (int i = 0; i < 27; i++) {
                    inv.setItem(i, finalIcon);
                }
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                BukkitTask grant = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    player.closeInventory();
                    GRANT_TASKS.remove(player.getUniqueId());
                    RewardHandler.give(player, finalReward, crateManager, plugin);
                }, 25L);
                GRANT_TASKS.put(player.getUniqueId(), grant);
            }
        }, 1L, crateManager.getAnimationTicks());
        ANIM_TASKS.put(player.getUniqueId(), task);
        taskRef.set(task);
    }

    public static class GuiListener implements Listener {

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            if (!OPENING.contains(event.getWhoClicked().getUniqueId())) {
                return;
            }
            if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getView().getTopInventory())) {
                return;
            }
            event.setCancelled(true);
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent event) {
            UUID uuid = event.getPlayer().getUniqueId();
            BukkitTask anim = ANIM_TASKS.remove(uuid);
            if (anim != null) {
                anim.cancel();
            }
            BukkitTask grant = GRANT_TASKS.remove(uuid);
            if (grant != null) {
                grant.cancel();
            }
            OPENING.remove(uuid);
        }
    }

    private static ItemStack buildFallback(Reward reward) {
        switch (reward.getType()) {
            case MONEY:
                ItemStack emerald = new ItemStack(Material.EMERALD);
                setDisplayName(emerald, "&eCoins: &f" + reward.getAmount());
                return emerald;
            case COMMAND:
                ItemStack paper = new ItemStack(Material.PAPER);
                setDisplayName(paper, "&fReward");
                return paper;
            default:
                return new ItemStack(Material.STONE);
        }
    }

    private static void setDisplayName(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Message.color(name));
            item.setItemMeta(meta);
        }
    }
}
