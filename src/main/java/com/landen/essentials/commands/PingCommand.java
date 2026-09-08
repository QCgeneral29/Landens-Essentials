package com.landen.essentials.commands;

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

public class PingCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtil.format("&cThis command can only be executed by players."));
            return true;
        }

        if (!player.hasPermission("landensessentials.ping")) {
            player.sendMessage(ColorUtil.format("&cYou do not have permission to use this command."));
            return true;
        }

        if (args.length > 1) {
            player.sendMessage(ColorUtil.format("&cUsage: /ping [username]"));
            return true;
        }

        Player target = player;
        if (args.length == 1) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null || !target.isOnline() || !player.canSee(target)) {
                player.sendMessage(ColorUtil.format("&cPlayer '" + args[0] + "' not found or is offline."));
                return true;
            }
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ColorUtil.format("&aYour ping: &e" + target.getPing() + "ms"));
        } else {
            player.sendMessage(ColorUtil.format("&a" + target.getName() + "'s ping: &e" + target.getPing() + "ms"));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && sender instanceof Player player) {
            String search = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .filter(player::canSee)
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(search))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
