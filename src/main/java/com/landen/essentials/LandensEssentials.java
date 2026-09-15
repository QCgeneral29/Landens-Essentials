package com.landen.essentials;

import com.landen.essentials.afk.AfkManager;
import com.landen.essentials.commands.AfkCommand;
import com.landen.essentials.commands.MotdCommand;
import com.landen.essentials.commands.PingCommand;
import com.landen.essentials.commands.ReloadCommand;
import com.landen.essentials.commands.SpawnCommand;
import com.landen.essentials.commands.TpAcceptCommand;
import com.landen.essentials.commands.TpaCancelCommand;
import com.landen.essentials.commands.TpDenyCommand;
import com.landen.essentials.commands.TpHereCommand;
import com.landen.essentials.commands.TpaCommand;
import com.landen.essentials.listeners.AfkListener;
import com.landen.essentials.listeners.EndermanListener;
import com.landen.essentials.listeners.PlayerListener;
import com.landen.essentials.motd.MotdManager;
import com.landen.essentials.teleport.TeleportManager;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class LandensEssentials extends JavaPlugin {

    private TeleportManager teleportManager;
    private MotdManager motdManager;
    private AfkManager afkManager;
    private EndermanListener endermanListener;
    private SpawnCommand spawnCommand;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.teleportManager = new TeleportManager(this);
        this.motdManager = new MotdManager(this);
        this.afkManager = new AfkManager(this);
        this.endermanListener = new EndermanListener(this);
        this.spawnCommand = new SpawnCommand(this);

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(this.endermanListener, this);
        getServer().getPluginManager().registerEvents(new AfkListener(this), this);

        registerCommands();

        getLogger().info("Landen's Essentials has been successfully enabled.");
    }

    @Override
    public void onDisable() {
        if (teleportManager != null) {
            teleportManager.shutdown();
        }
        if (afkManager != null) {
            afkManager.shutdown();
        }
        getLogger().info("Landen's Essentials has been disabled.");
    }

    public void reloadPlugin() {
        reloadConfig();
        if (teleportManager != null) {
            teleportManager.loadConfig();
        }
        if (motdManager != null) {
            motdManager.loadConfig();
        }
        if (afkManager != null) {
            afkManager.loadConfig();
        }
        if (endermanListener != null) {
            endermanListener.loadConfig();
        }
        if (spawnCommand != null) {
            spawnCommand.loadConfig();
        }
        getLogger().info("Landen's Essentials configuration reloaded successfully.");
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

        PluginCommand tpacancelCmd = getCommand("tpacancel");
        if (tpacancelCmd != null) {
            TpaCancelCommand executor = new TpaCancelCommand(this);
            tpacancelCmd.setExecutor(executor);
            tpacancelCmd.setTabCompleter(executor);
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
            spawnCmd.setExecutor(this.spawnCommand);
            spawnCmd.setTabCompleter(this.spawnCommand);
        }

        PluginCommand afkCmd = getCommand("afk");
        if (afkCmd != null) {
            AfkCommand executor = new AfkCommand(this);
            afkCmd.setExecutor(executor);
            afkCmd.setTabCompleter(executor);
        }

        PluginCommand pingCmd = getCommand("ping");
        if (pingCmd != null) {
            PingCommand executor = new PingCommand();
            pingCmd.setExecutor(executor);
            pingCmd.setTabCompleter(executor);
        }

        PluginCommand reloadCmd = getCommand("reload-landens-essentials");
        if (reloadCmd != null) {
            ReloadCommand executor = new ReloadCommand(this);
            reloadCmd.setExecutor(executor);
            reloadCmd.setTabCompleter(executor);
        }
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public MotdManager getMotdManager() {
        return motdManager;
    }

    public AfkManager getAfkManager() {
        return afkManager;
    }
}
