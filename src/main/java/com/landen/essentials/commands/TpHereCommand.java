package com.landen.essentials.commands;

import com.landen.essentials.LandensEssentials;
import com.landen.essentials.teleport.TeleportRequest;
import com.landen.essentials.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TpHereCommand implements CommandExecutor, TabCompleter {

    private final LandensEssentials plugin;

    public TpHereCommand(LandensEssentials plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtil.format("&cThis command can only be executed by players."));
            return true;
        }

        if (!player.hasPermission("landensessentials.tpahere")) {
            player.sendMessage(ColorUtil.format("&cYou do not have permission to use this command."));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ColorUtil.format("&cUsage: /tpahere <username>"));
            return true;
        }

        String targetName = args[0];
        Player targetPlayer = Bukkit.getPlayer(targetName);

        if (targetPlayer == null || !targetPlayer.isOnline() || !player.canSee(targetPlayer)) {
            player.sendMessage(ColorUtil.format("&cPlayer '" + targetName + "' not found or is offline."));
            return true;
        }

        plugin.getTeleportManager().sendRequest(player, targetPlayer, TeleportRequest.Type.TPA_HERE);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && sender instanceof Player player) {
            String search = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .filter(player::canSee)
                    .filter(target -> !target.getUniqueId().equals(player.getUniqueId()))
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(search))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
