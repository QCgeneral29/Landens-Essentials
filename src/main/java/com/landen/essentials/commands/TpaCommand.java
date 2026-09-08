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

public class TpaCommand implements CommandExecutor, TabCompleter {

    private final LandensEssentials plugin;

    public TpaCommand(LandensEssentials plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtil.format("&cThis command can only be executed by players."));
            return true;
        }

        if (!player.hasPermission("landensessentials.tpa")) {
            player.sendMessage(ColorUtil.format("&cYou do not have permission to use this command."));
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String targetName = args[0];
        Player targetPlayer = Bukkit.getPlayer(targetName);

        if (targetPlayer == null || !targetPlayer.isOnline() || !player.canSee(targetPlayer)) {
            player.sendMessage(ColorUtil.format("&cPlayer '" + targetName + "' not found or is offline."));
            return true;
        }

        plugin.getTeleportManager().sendRequest(player, targetPlayer, TeleportRequest.Type.TPA);
        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(ColorUtil.format("&e========== &bLanden's Essentials Teleport Commands &e=========="));
        player.sendMessage(ColorUtil.format("&e/tpa &7- Display available teleportation commands."));
        player.sendMessage(ColorUtil.format("&e/tpa <username> &7- Send a request to teleport to a player."));
        player.sendMessage(ColorUtil.format("&e/tpaccept &7- Accept a pending teleport request."));
        player.sendMessage(ColorUtil.format("&e/tpadeny &7- Deny a pending teleport request."));
        player.sendMessage(ColorUtil.format("&e/tpahere <username> &7- Send a request for a player to teleport to you."));
        player.sendMessage(ColorUtil.format("&e=========================================================="));
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
