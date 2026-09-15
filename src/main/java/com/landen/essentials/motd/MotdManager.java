package com.landen.essentials.motd;

import com.landen.essentials.LandensEssentials;
import com.landen.essentials.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.List;

public class MotdManager {

    private final LandensEssentials plugin;
    private boolean enabled;
    private List<String> messageLines;

    public MotdManager(LandensEssentials plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        this.enabled = plugin.getConfig().getBoolean("motd.enabled", true);
        this.messageLines = plugin.getConfig().getStringList("motd.message");
    }

    public void sendMotd(Player player) {
        if (!enabled || messageLines == null || messageLines.isEmpty()) {
            return;
        }

        for (String rawLine : messageLines) {
            String formattedLine = replacePlaceholders(rawLine, player);
            player.sendMessage(ColorUtil.format(formattedLine));
        }
    }

    private String replacePlaceholders(String line, Player player) {
        if (line == null) {
            return "";
        }

        long ticksPlayed = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
        long totalSeconds = ticksPlayed / 20L;
        long hours = totalSeconds / 3600L;
        long minutes = (totalSeconds % 3600L) / 60L;
        String formattedPlaytime = hours + "h " + minutes + "m";

        return line
                .replace("{username}", player.getName())
                .replace("{player}", player.getName())
                .replace("{playtime}", formattedPlaytime)
                .replace("{online}", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .replace("{max_online}", String.valueOf(Bukkit.getMaxPlayers()))
                .replace("{world}", getFriendlyWorldName(player));
    }

    /**
     * Returns a user-friendly world name.
     * world -> "Overworld"
     * world_nether -> "Neither"
     * world_the_end -> "The End"
     */
    private String getFriendlyWorldName(Player player) {
        String worldName = player.getWorld().getName().toLowerCase();
        switch (worldName) {
            case "world":
                return "Overworld";
            case "world_nether":
                return "Neither";
            case "world_the_end":
                return "The End";
            default:
                return player.getWorld().getName();
        }
    }
}
