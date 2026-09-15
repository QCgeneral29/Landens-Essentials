package com.landen.essentials.listeners;

import com.landen.essentials.LandensEssentials;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class EndermanListener implements Listener {

    private final LandensEssentials plugin;
    private boolean preventGriefing;

    public EndermanListener(LandensEssentials plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    public void loadConfig() {
        this.preventGriefing = plugin.getConfig().getBoolean("endermen.prevent-griefing", true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEndermanChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.ENDERMAN) {
            if (preventGriefing) {
                event.setCancelled(true);
            }
        }
    }
}
