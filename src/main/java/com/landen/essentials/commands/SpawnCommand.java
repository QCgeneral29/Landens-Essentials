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
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;

public class SpawnCommand implements CommandExecutor, TabCompleter {

    private final LandensEssentials plugin;
    // Track players waiting for a spawn teleport to avoid spamming
    private static final Set<UUID> pendingSpawnTeleports = ConcurrentHashMap.newKeySet();

    public SpawnCommand(LandensEssentials plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
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
                plugin.getTeleportManager().getSafeSearchRadius());
        if (targetLocation == null) {
            targetLocation = spawnLocation;
        }

        final Location finalTargetLocation = targetLocation;

        int delaySeconds = plugin.getConfig().getInt("teleport.spawn-teleport-delay-seconds", 5);

        if (pendingSpawnTeleports.contains(player.getUniqueId())) {
            player.sendMessage(ColorUtil.format("&cYou are already waiting to teleport."));
            return true;
        }
        if (delaySeconds <= 0) {
            // Immediate teleport
            player.teleportAsync(finalTargetLocation).thenAccept(success -> {
                if (success) {
                    player.sendMessage(ColorUtil.format("&aTeleported to spawn."));
                } else {
                    player.sendMessage(ColorUtil.format("&cFailed to teleport to spawn."));
                }
            });
            return true;
        }

        // Delayed teleport with movement cancellation
        pendingSpawnTeleports.add(player.getUniqueId());
        Location initialLocation = player.getLocation().clone();
        player.sendMessage(ColorUtil.format("&aTeleporting to spawn in " + delaySeconds + " seconds. Do not move."));

        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = delaySeconds * 20;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    pendingSpawnTeleports.remove(player.getUniqueId());
                    cancel();
                    return;
                }
                // Check if player moved
                if (player.getLocation().distanceSquared(initialLocation) > 0.01) {
                    player.sendMessage(ColorUtil.format("&cTeleport cancelled because you moved."));
                    pendingSpawnTeleports.remove(player.getUniqueId());
                    cancel();
                    return;
                }
                ticks++;
                if (ticks >= maxTicks) {
                    player.teleportAsync(finalTargetLocation).thenAccept(success -> {
                        if (success) {
                            player.sendMessage(ColorUtil.format("&aTeleported to spawn."));
                        } else {
                            player.sendMessage(ColorUtil.format("&cFailed to teleport to spawn."));
                        }
                        // Remove from pending after teleport attempt finishes
                        pendingSpawnTeleports.remove(player.getUniqueId());
                    });
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
