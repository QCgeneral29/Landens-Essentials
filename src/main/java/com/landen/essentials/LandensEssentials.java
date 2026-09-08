package com.landen.essentials;

import com.landen.essentials.commands.MotdCommand;
import com.landen.essentials.commands.SpawnCommand;
import com.landen.essentials.commands.TpAcceptCommand;
import com.landen.essentials.commands.TpDenyCommand;
import com.landen.essentials.commands.TpHereCommand;
import com.landen.essentials.commands.TpaCommand;
import com.landen.essentials.listeners.EndermanListener;
import com.landen.essentials.listeners.PlayerListener;
import com.landen.essentials.motd.MotdManager;
import com.landen.essentials.teleport.TeleportManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class LandensEssentials extends JavaPlugin {

    private TeleportManager teleportManager;
    private MotdManager motdManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.teleportManager = new TeleportManager(this);
        this.motdManager = new MotdManager(this);

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new EndermanListener(this), this);

        registerCommands();

        getLogger().info("Landen's Essentials has been successfully enabled.");
    }

    @Override
    public void onDisable() {
        if (teleportManager != null) {
            teleportManager.shutdown();
        }
        getLogger().info("Landen's Essentials has been disabled.");
    }

    private void registerCommands() {
        PluginCommand tpaCmd = getCommand("tpa");
        if (tpaCmd != null) {
            TpaCommand executor = new TpaCommand(this);
            tpaCmd.setExecutor(executor);
            tpaCmd.setTabCompleter(executor);
        }

        PluginCommand tpacceptCmd = getCommand("tpaccept");
        if (tpacceptCmd != null) {
            TpAcceptCommand executor = new TpAcceptCommand(this);
            tpacceptCmd.setExecutor(executor);
            tpacceptCmd.setTabCompleter(executor);
        }

        PluginCommand tpadenyCmd = getCommand("tpadeny");
        if (tpadenyCmd != null) {
            TpDenyCommand executor = new TpDenyCommand(this);
            tpadenyCmd.setExecutor(executor);
            tpadenyCmd.setTabCompleter(executor);
        }

        PluginCommand tpahereCmd = getCommand("tpahere");
        if (tpahereCmd != null) {
            TpHereCommand executor = new TpHereCommand(this);
            tpahereCmd.setExecutor(executor);
            tpahereCmd.setTabCompleter(executor);
        }

        PluginCommand motdCmd = getCommand("motd");
        if (motdCmd != null) {
            MotdCommand executor = new MotdCommand(this);
            motdCmd.setExecutor(executor);
            motdCmd.setTabCompleter(executor);
        }

        PluginCommand spawnCmd = getCommand("spawn");
        if (spawnCmd != null) {
            SpawnCommand executor = new SpawnCommand(this);
            spawnCmd.setExecutor(executor);
            spawnCmd.setTabCompleter(executor);
        }
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public MotdManager getMotdManager() {
        return motdManager;
    }
}
