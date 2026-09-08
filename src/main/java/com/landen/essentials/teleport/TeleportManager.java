package com.landen.essentials.teleport;

import com.landen.essentials.LandensEssentials;
import com.landen.essentials.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportManager {

    private final LandensEssentials plugin;
    private final Map<UUID, TeleportRequest> pendingRequests = new ConcurrentHashMap<>();
    private BukkitTask cleanupTask;

    public TeleportManager(LandensEssentials plugin) {
        this.plugin = plugin;
        startCleanupTask();
    }

    private void startCleanupTask() {
        cleanupTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::cleanExpiredRequests, 400L, 400L);
    }

    public void shutdown() {
        if (cleanupTask != null) {
            cleanupTask.cancel();
        }
        pendingRequests.clear();
    }

    public long getTimeoutMillis() {
        long seconds = plugin.getConfig().getLong("teleport.request-timeout-seconds", 60);
        return seconds * 1000L;
    }

    public int getSafeSearchRadius() {
        return plugin.getConfig().getInt("teleport.safe-search-radius", 4);
    }

    public boolean sendRequest(Player requester, Player target, TeleportRequest.Type type) {
        if (requester.getUniqueId().equals(target.getUniqueId())) {
            requester.sendMessage(ColorUtil.format("&cYou cannot send a teleport request to yourself!"));
            return false;
        }

        TeleportRequest request = new TeleportRequest(requester.getUniqueId(), target.getUniqueId(), type);
        pendingRequests.put(target.getUniqueId(), request);

        int timeoutSeconds = plugin.getConfig().getInt("teleport.request-timeout-seconds", 60);

        if (type == TeleportRequest.Type.TPA) {
            requester.sendMessage(ColorUtil.format(
                    "&aTeleport request sent to &f" + target.getName() + "&a. Expires in " + timeoutSeconds + "s."));
            target.sendMessage(ColorUtil.format("&f" + requester.getName() + " &ahas requested to teleport to you."));
            target.sendMessage(ColorUtil.format("&aType &e/tpaccept &ato accept or &c/tpadeny &ato deny."));
        } else {
            requester.sendMessage(ColorUtil.format("&aTeleport-here request sent to &f" + target.getName()
                    + "&a. Expires in " + timeoutSeconds + "s."));
            target.sendMessage(
                    ColorUtil.format("&f" + requester.getName() + " &ahas requested you to teleport to them."));
            target.sendMessage(ColorUtil.format("&aType &e/tpaccept &ato accept or &c/tpadeny &ato deny."));
        }

        if (plugin.getAfkManager() != null && plugin.getAfkManager().isAfk(target)) {
            requester.sendMessage(ColorUtil.format("&eNote: &f" + target.getName() + " &eis currently AFK."));
        }

        return true;
    }

    public boolean acceptRequest(Player target) {
        TeleportRequest request = pendingRequests.get(target.getUniqueId());

        if (request == null || request.isExpired(getTimeoutMillis())) {
            pendingRequests.remove(target.getUniqueId());
            target.sendMessage(ColorUtil.format("&cYou have no active teleport requests."));
            return false;
        }

        Player requester = Bukkit.getPlayer(request.getRequesterUuid());
        if (requester == null || !requester.isOnline()) {
            pendingRequests.remove(target.getUniqueId());
            target.sendMessage(ColorUtil.format("&cThe player who sent the request is no longer online."));
            return false;
        }

        Player playerToTeleport;
        Location destinationLocation;

        if (request.getType() == TeleportRequest.Type.TPA) {
            playerToTeleport = requester;
            destinationLocation = target.getLocation();
        } else {
            playerToTeleport = target;
            destinationLocation = requester.getLocation();
        }

        Location safeLocation = SafeTeleportUtil.findSafeLocation(destinationLocation, getSafeSearchRadius());

        if (safeLocation == null) {
            String msg = "&cTeleport cancelled: Destination is not safe!";
            target.sendMessage(ColorUtil.format(msg));
            requester.sendMessage(ColorUtil.format(msg));
            pendingRequests.remove(target.getUniqueId());
            return false;
        }

        pendingRequests.remove(target.getUniqueId());

        Bukkit.getScheduler().runTask(plugin, () -> {
            playerToTeleport.teleportAsync(safeLocation).thenAccept(success -> {
                if (success) {
                    playerToTeleport.sendMessage(ColorUtil.format("&aTeleporting..."));
                    if (playerToTeleport.equals(requester)) {
                        target.sendMessage(
                                ColorUtil.format("&aAccepted teleport request from &f" + requester.getName() + "&a."));
                    } else {
                        requester.sendMessage(
                                ColorUtil.format("&f" + target.getName() + " &aaccepted your teleport request."));
                    }
                } else {
                    playerToTeleport.sendMessage(ColorUtil.format("&cTeleport failed."));
                }
            });
        });

        return true;
    }

    public boolean denyRequest(Player target) {
        TeleportRequest request = pendingRequests.remove(target.getUniqueId());

        if (request == null || request.isExpired(getTimeoutMillis())) {
            target.sendMessage(ColorUtil.format("&cYou have no active teleport requests."));
            return false;
        }

        target.sendMessage(ColorUtil.format("&cTeleport request denied."));

        Player requester = Bukkit.getPlayer(request.getRequesterUuid());
        if (requester != null && requester.isOnline()) {
            requester.sendMessage(ColorUtil.format("&f" + target.getName() + " &cdenied your teleport request."));
        }

        return true;
    }

    public void removeRequestsForPlayer(UUID uuid) {
        pendingRequests.remove(uuid);
        pendingRequests.values().removeIf(req -> req.getRequesterUuid().equals(uuid));
    }

    private void cleanExpiredRequests() {
        long timeout = getTimeoutMillis();
        pendingRequests.entrySet().removeIf(entry -> entry.getValue().isExpired(timeout));
    }
}
