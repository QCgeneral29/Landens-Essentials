package com.landen.essentials.afk;

import com.landen.essentials.LandensEssentials;
import com.landen.essentials.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AfkManager {

    private final LandensEssentials plugin;
    private final Set<UUID> afkPlayers = ConcurrentHashMap.newKeySet();
    private final Map<UUID, Long> lastActivity = new ConcurrentHashMap<>();
    private BukkitTask autoAfkTask;

    public AfkManager(LandensEssentials plugin) {
        this.plugin = plugin;
        long now = System.currentTimeMillis();
        for (Player player : Bukkit.getOnlinePlayers()) {
            lastActivity.put(player.getUniqueId(), now);
        }
        startAutoAfkTask();
    }

    private void startAutoAfkTask() {
        this.autoAfkTask = Bukkit.getScheduler().runTaskTimer(plugin, this::checkAutoAfk, 20L, 20L);
    }

    public boolean isAfk(Player player) {
        return player != null && isAfk(player.getUniqueId());
    }

    public boolean isAfk(UUID uuid) {
        return afkPlayers.contains(uuid);
    }

    public void setAfk(Player player, boolean afk) {
        if (player == null) {
            return;
        }

        if (!Bukkit.isPrimaryThread()) {
            Bukkit.getScheduler().runTask(plugin, () -> setAfk(player, afk));
            return;
        }

        UUID uuid = player.getUniqueId();
        boolean currentlyAfk = isAfk(uuid);

        if (afk == currentlyAfk) {
            return;
        }

        boolean broadcast = plugin.getConfig().getBoolean("afk.broadcast", true);

        if (afk) {
            afkPlayers.add(uuid);

            String tabFormat = plugin.getConfig().getString("afk.tab-format", "&7AFK-&f{username}");
            String formattedTab = tabFormat.replace("{username}", player.getName());
            player.playerListName(ColorUtil.format(formattedTab));

            if (broadcast) {
                Bukkit.broadcast(ColorUtil.format("&7* &f" + player.getName() + " &7is now AFK."));
            } else {
                player.sendMessage(ColorUtil.format("&7You are now AFK."));
            }
        } else {
            afkPlayers.remove(uuid);
            lastActivity.put(uuid, System.currentTimeMillis());

            player.playerListName(null);

            if (broadcast) {
                Bukkit.broadcast(ColorUtil.format("&7* &f" + player.getName() + " &7is no longer AFK."));
            } else {
                player.sendMessage(ColorUtil.format("&7You are no longer AFK."));
            }
        }
    }

    public void toggleAfk(Player player) {
        if (player == null) {
            return;
        }
        setAfk(player, !isAfk(player));
    }

    public void updateActivity(Player player) {
        if (player == null) {
            return;
        }

        lastActivity.put(player.getUniqueId(), System.currentTimeMillis());

        if (isAfk(player)) {
            setAfk(player, false);
        }
    }

    public void handleJoin(Player player) {
        if (player == null) {
            return;
        }
        lastActivity.put(player.getUniqueId(), System.currentTimeMillis());
        afkPlayers.remove(player.getUniqueId());
    }

    public void handleQuit(Player player) {
        if (player == null) {
            return;
        }
        afkPlayers.remove(player.getUniqueId());
        lastActivity.remove(player.getUniqueId());
    }

    private void checkAutoAfk() {
        long autoAfkSeconds = plugin.getConfig().getLong("afk.auto-afk-seconds", 60L);
        if (autoAfkSeconds <= 0) {
            return;
        }

        long now = System.currentTimeMillis();
        long thresholdMillis = autoAfkSeconds * 1000L;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isAfk(player)) {
                continue;
            }

            long last = lastActivity.getOrDefault(player.getUniqueId(), now);
            if (now - last >= thresholdMillis) {
                setAfk(player, true);
            }
        }
    }

    public void shutdown() {
        if (autoAfkTask != null) {
            autoAfkTask.cancel();
        }

        for (UUID uuid : afkPlayers) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                player.playerListName(null);
            }
        }

        afkPlayers.clear();
        lastActivity.clear();
    }
}
