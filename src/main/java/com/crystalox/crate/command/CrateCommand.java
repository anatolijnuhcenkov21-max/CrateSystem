package com.crystalox.crate.command;

import com.crystalox.crate.crate.Crate;
import com.crystalox.crate.crate.CrateManager;
import com.crystalox.crate.storage.CrateBlockStorage;
import com.crystalox.crate.gui.CrateGui;
import com.crystalox.crate.util.ItemParser;
import com.crystalox.crate.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CrateCommand implements CommandExecutor, TabCompleter {

    private static final List<String> SUBCOMMANDS = Arrays.asList("givekey", "setblock", "removeblock", "list", "open", "reload");

    private final JavaPlugin plugin;
    private final CrateManager crateManager;
    private final CrateBlockStorage storage;

    public CrateCommand(JavaPlugin plugin, CrateManager crateManager, CrateBlockStorage storage) {
        this.plugin = plugin;
        this.crateManager = crateManager;
        this.storage = storage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(msg("usage"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "givekey":
                giveKey(sender, args);
                break;
            case "setblock":
                setBlock(sender, args);
                break;
            case "removeblock":
                removeBlock(sender, args);
                break;
            case "list":
                list(sender);
                break;
            case "open":
                open(sender, args);
                break;
            case "reload":
                reload(sender);
                break;
            default:
                sender.sendMessage(msg("usage"));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return subcommands(args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("givekey")) {
            return players(args[1]);
        }
        if (args.length == 3 && (args[0].equalsIgnoreCase("givekey")
                || args[0].equalsIgnoreCase("setblock")
                || args[0].equalsIgnoreCase("open"))) {
            return crateNames(args[2]);
        }
        return new ArrayList<String>();
    }

    private void giveKey(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(msg("usage"));
            return;
        }
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(msg("player-not-found").replace("%player%", args[1]));
            return;
        }
        Crate crate = crateManager.getCrate(args[2]);
        if (crate == null) {
            sender.sendMessage(msg("crate-not-found").replace("%crate%", args[2]));
            return;
        }
        if (!admin(sender)) {
            return;
        }
        ItemStack key = crate.getKeyItem().clone();
        key.setAmount(amount(args));
        Map<Integer, ItemStack> leftover = target.getInventory().addItem(key);
        for (ItemStack drop : leftover.values()) {
            target.getWorld().dropItemNaturally(target.getLocation(), drop);
        }
        sender.sendMessage(msg("key-given")
                .replace("%player%", target.getName())
                .replace("%crate%", crate.getDisplayName())
                .replace("%amount%", String.valueOf(key.getAmount())));
        target.sendMessage(msg("key-received")
                .replace("%crate%", crate.getDisplayName())
                .replace("%amount%", String.valueOf(key.getAmount())));
    }

    private void setBlock(CommandSender sender, String[] args) {
        if (!admin(sender)) {
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(msg("usage"));
            return;
        }
        Crate crate = crateManager.getCrate(args[1]);
        if (crate == null) {
            sender.sendMessage(msg("crate-not-found").replace("%crate%", args[1]));
            return;
        }
        Player player = player(sender);
        if (player == null) {
            return;
        }
        Block block = player.getTargetBlockExact(5);
        if (block == null) {
            sender.sendMessage(msg("block-not-found"));
            return;
        }
        storage.setBlock(block.getLocation(), args[1]);
        sender.sendMessage(msg("block-set").replace("%crate%", crate.getDisplayName()));
    }

    private void removeBlock(CommandSender sender, String[] args) {
        if (!admin(sender)) {
            return;
        }
        Player player = player(sender);
        if (player == null) {
            return;
        }
        Block block = player.getTargetBlockExact(5);
        if (block == null || storage.getCrateId(block.getLocation()) == null) {
            sender.sendMessage(msg("block-not-found"));
            return;
        }
        storage.removeBlock(block.getLocation());
        sender.sendMessage(msg("block-removed"));
    }

    private void list(CommandSender sender) {
        sender.sendMessage(msg("list-header"));
        for (Crate crate : crateManager.getCrates()) {
            sender.sendMessage(msg("list-entry")
                    .replace("%crate%", crate.getDisplayName())
                    .replace("%rewards%", String.valueOf(crate.getRewards().size())));
        }
    }

    private void open(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(msg("usage"));
            return;
        }
        Crate crate = crateManager.getCrate(args[1]);
        if (crate == null) {
            sender.sendMessage(msg("crate-not-found").replace("%crate%", args[1]));
            return;
        }
        Player player = player(sender);
        if (player == null) {
            return;
        }
        if (!player.hasPermission("crystalox.crate.use")) {
            player.sendMessage(msg("no-permission"));
            return;
        }
        if (player.hasPermission("crystalox.crate.admin")) {
            CrateGui.open(player, crate, plugin, crateManager);
            return;
        }
        if (CrateGui.isOpening(player)) {
            return;
        }
        ItemStack held = player.getInventory().getItemInMainHand();
        if (held == null || !ItemParser.matches(held, crate.getKeyItem())) {
            player.sendMessage(msg("need-key").replace("%crate%", crate.getDisplayName()));
            return;
        }
        if (held.getAmount() > 1) {
            held.setAmount(held.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(null);
        }
        player.updateInventory();
        CrateGui.open(player, crate, plugin, crateManager);
    }

    private void reload(CommandSender sender) {
        if (!admin(sender)) {
            return;
        }
        crateManager.reload();
        sender.sendMessage(msg("reloaded"));
    }

    private boolean admin(CommandSender sender) {
        if (sender.hasPermission("crystalox.crate.admin")) {
            return true;
        }
        sender.sendMessage(msg("no-permission"));
        return false;
    }

    private Player player(CommandSender sender) {
        if (sender instanceof Player) {
            return (Player) sender;
        }
        sender.sendMessage(msg("usage"));
        return null;
    }

    private int amount(String[] args) {
        if (args.length < 4) {
            return 1;
        }
        try {
            return Math.max(1, Integer.parseInt(args[3]));
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private List<String> subcommands(String prefix) {
        List<String> result = new ArrayList<String>();
        for (String sub : SUBCOMMANDS) {
            if (sub.startsWith(prefix.toLowerCase())) {
                result.add(sub);
            }
        }
        return result;
    }

    private List<String> players(String prefix) {
        List<String> result = new ArrayList<String>();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.getName().toLowerCase().startsWith(prefix.toLowerCase())) {
                result.add(online.getName());
            }
        }
        return result;
    }

    private List<String> crateNames(String prefix) {
        List<String> result = new ArrayList<String>();
        for (Crate crate : crateManager.getCrates()) {
            if (crate.getId().toLowerCase().startsWith(prefix.toLowerCase())) {
                result.add(crate.getId());
            }
        }
        return result;
    }

    private String msg(String key) {
        return Message.of(crateManager.getMessages(), key);
    }
}
