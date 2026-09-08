package com.landen.essentials.commands;

import com.landen.essentials.LandensEssentials;
import com.landen.essentials.teleport.SafeTeleportUtil;
import com.landen.essentials.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SpawnCommand implements CommandExecutor, TabCompleter {

    private final LandensEssentials plugin;

    public SpawnCommand(LandensEssentials plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ColorUtil.format("&cThis command can only be executed by players."));
            return true;
        }

        if (!player.hasPermission("landensessentials.spawn")) {
            player.sendMessage(ColorUtil.format("&cYou do not have permission to use this command."));
            return true;
        }

        World overworld = Bukkit.getWorlds().stream()
                .filter(w -> w.getEnvironment() == World.Environment.NORMAL)
                .findFirst()
                .orElse(Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0));

        if (overworld == null) {
            player.sendMessage(ColorUtil.format("&cOverworld could not be found!"));
            return true;
        }

        Location spawnLocation = overworld.getSpawnLocation().clone();
        if (spawnLocation.getX() == spawnLocation.getBlockX()) {
            spawnLocation.add(0.5, 0, 0.5);
        }

        Location targetLocation = SafeTeleportUtil.findSafeLocation(
                spawnLocation,
                plugin.getTeleportManager().getSafeSearchRadius()
        );
        if (targetLocation == null) {
            targetLocation = spawnLocation;
        }

        player.teleportAsync(targetLocation).thenAccept(success -> {
            if (success) {
                player.sendMessage(ColorUtil.format("&aTeleported to spawn."));
            } else {
                player.sendMessage(ColorUtil.format("&cFailed to teleport to spawn."));
            }
        });

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
