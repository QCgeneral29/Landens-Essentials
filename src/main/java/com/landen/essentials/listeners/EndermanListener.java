package com.landen.essentials.listeners;

import com.landen.essentials.LandensEssentials;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class EndermanListener implements Listener {

    private final LandensEssentials plugin;

    public EndermanListener(LandensEssentials plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEndermanChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.ENDERMAN) {
            boolean preventGriefing = plugin.getConfig().getBoolean("endermen.prevent-griefing", true);
            if (preventGriefing) {
                event.setCancelled(true);
            }
        }
    }
}
