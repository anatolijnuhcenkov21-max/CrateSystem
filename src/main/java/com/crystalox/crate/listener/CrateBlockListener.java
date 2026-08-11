package com.crystalox.crate.listener;

import com.crystalox.crate.crate.Crate;
import com.crystalox.crate.crate.CrateManager;
import com.crystalox.crate.storage.CrateBlockStorage;
import com.crystalox.crate.gui.CrateGui;
import com.crystalox.crate.util.ItemParser;
import com.crystalox.crate.util.Message;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class CrateBlockListener implements Listener {

    private final JavaPlugin plugin;
    private final CrateManager crateManager;
    private final CrateBlockStorage storage;

    public CrateBlockListener(JavaPlugin plugin, CrateManager crateManager, CrateBlockStorage storage) {
        this.plugin = plugin;
        this.crateManager = crateManager;
        this.storage = storage;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }
        String crateId = storage.getCrateId(block.getLocation());
        if (crateId == null) {
            return;
        }
        event.setCancelled(true);
        Crate crate = crateManager.getCrate(crateId);
        if (crate == null) {
            return;
        }
        Player player = event.getPlayer();
        if (player.hasPermission("crystalox.crate.admin")) {
            CrateGui.open(player, crate, plugin, crateManager);
            return;
        }
        if (!hasKey(player, crate)) {
            player.sendMessage(msg("need-key").replace("%crate%", crate.getDisplayName()));
            return;
        }
        consumeKey(player);
        CrateGui.open(player, crate, plugin, crateManager);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (storage.getCrateId(event.getBlock().getLocation()) == null) {
            return;
        }
        if (event.getPlayer().hasPermission("crystalox.crate.admin")) {
            storage.removeBlock(event.getBlock().getLocation());
            event.getPlayer().sendMessage(msg("block-removed"));
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(this::isCrateBlock);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(this::isCrateBlock);
    }

    private boolean hasKey(Player player, Crate crate) {
        ItemStack held = player.getInventory().getItemInMainHand();
        return held != null && ItemParser.matches(held, crate.getKeyItem());
    }

    private void consumeKey(Player player) {
        ItemStack held = player.getInventory().getItemInMainHand();
        if (held.getAmount() > 1) {
            held.setAmount(held.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }
        player.updateInventory();
    }

    private boolean isCrateBlock(Block block) {
        return storage.getCrateId(block.getLocation()) != null;
    }

    private String msg(String key) {
        return Message.of(crateManager.getMessages(), key);
    }
}
