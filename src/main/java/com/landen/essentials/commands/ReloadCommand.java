package com.landen.essentials.commands;

import com.landen.essentials.LandensEssentials;
import com.landen.essentials.utils.ColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ReloadCommand implements CommandExecutor, TabCompleter {

    private final LandensEssentials plugin;

    public ReloadCommand(LandensEssentials plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!sender.hasPermission("landensessentials.reload")) {
            sender.sendMessage(ColorUtil.format("&cYou do not have permission to use this command."));
            return true;
        }

        plugin.reloadPlugin();
        sender.sendMessage(ColorUtil.format("&aLanden's Essentials configuration reloaded successfully."));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
