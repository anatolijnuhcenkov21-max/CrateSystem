package com.crystalox.crate.crate;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class CrateManager {

    private final File file;
    private final Map<String, Crate> crates = new LinkedHashMap<String, Crate>();
    private YamlConfiguration config;

    public CrateManager(JavaPlugin plugin) {
        this.file = new File(plugin.getDataFolder(), "crates.yml");
        if (!file.exists()) {
            plugin.saveResource("crates.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        reload();
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
        crates.clear();
        ConfigurationSection section = config.getConfigurationSection("crates");
        if (section == null) {
            return;
        }
        for (String key : section.getKeys(false)) {
            crates.put(key.toLowerCase(), Crate.fromConfig(key, section.getConfigurationSection(key)));
        }
    }

    public Crate getCrate(String id) {
        return crates.get(id.toLowerCase());
    }

    public Collection<Crate> getCrates() {
        return Collections.unmodifiableCollection(crates.values());
    }

    public ConfigurationSection getMessages() {
        return config.getConfigurationSection("settings.messages");
    }

    public int getAnimationTicks() {
        return config.getInt("settings.animation-ticks", 5);
    }

    public int getAnimationCycles() {
        return config.getInt("settings.animation-cycles", 18);
    }
}
