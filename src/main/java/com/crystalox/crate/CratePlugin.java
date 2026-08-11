package com.crystalox.crate;

import com.crystalox.crate.command.CrateCommand;
import com.crystalox.crate.crate.CrateManager;
import com.crystalox.crate.gui.CrateGui;
import com.crystalox.crate.listener.CrateBlockListener;
import com.crystalox.crate.storage.CrateBlockStorage;
import org.bukkit.plugin.java.JavaPlugin;

public class CratePlugin extends JavaPlugin {

    private static CratePlugin instance;

    private CrateManager crateManager;
    private CrateBlockStorage blockStorage;

    public static CratePlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        crateManager = new CrateManager(this);
        blockStorage = new CrateBlockStorage(this);
        getServer().getPluginManager().registerEvents(new CrateBlockListener(this, crateManager, blockStorage), this);
        getServer().getPluginManager().registerEvents(new CrateGui.GuiListener(), this);
        CrateCommand crateCommand = new CrateCommand(this, crateManager, blockStorage);
        getCommand("crate").setExecutor(crateCommand);
        getCommand("crate").setTabCompleter(crateCommand);
        getLogger().info("CrateSystem v" + getDescription().getVersion() + " enabled");
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public CrateManager getCrateManager() {
        return crateManager;
    }

    public CrateBlockStorage getBlockStorage() {
        return blockStorage;
    }
}
