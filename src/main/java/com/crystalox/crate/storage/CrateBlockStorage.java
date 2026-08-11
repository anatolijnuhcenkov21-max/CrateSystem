package com.crystalox.crate.storage;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class CrateBlockStorage {

    private final JavaPlugin plugin;
    private final File file;
    private final YamlConfiguration config;

    public CrateBlockStorage(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "crates-data.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public String getCrateId(Location location) {
        return config.getString("blocks." + blockKey(location));
    }

    public void setBlock(Location location, String crateId) {
        config.set("blocks." + blockKey(location), crateId);
        save();
    }

    public void removeBlock(Location location) {
        config.set("blocks." + blockKey(location), null);
        save();
    }

    private String blockKey(Location location) {
        return location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to save crates-data.yml", e);
        }
    }
}
