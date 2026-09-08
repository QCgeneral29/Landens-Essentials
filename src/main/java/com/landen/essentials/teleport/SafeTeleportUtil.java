package com.landen.essentials.teleport;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.EnumSet;
import java.util.Set;

public final class SafeTeleportUtil {

    private static final Set<Material> HAZARDOUS_MATERIALS = EnumSet.of(
            Material.LAVA,
            Material.FIRE,
            Material.SOUL_FIRE,
            Material.CACTUS,
            Material.SWEET_BERRY_BUSH,
            Material.WITHER_ROSE,
            Material.POWDER_SNOW,
            Material.MAGMA_BLOCK
    );

    private SafeTeleportUtil() {
    }

    public static boolean isSafeLocation(Location location) {
        if (location == null || location.getWorld() == null) {
            return false;
        }

        World world = location.getWorld();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        if (y < world.getMinHeight() + 1 || y >= world.getMaxHeight() - 1) {
            return false;
        }

        Block footBlock = world.getBlockAt(x, y, z);
        Block headBlock = footBlock.getRelative(BlockFace.UP);
        Block groundBlock = footBlock.getRelative(BlockFace.DOWN);

        if (!groundBlock.getType().isSolid() || HAZARDOUS_MATERIALS.contains(groundBlock.getType())) {
            return false;
        }

        if (HAZARDOUS_MATERIALS.contains(footBlock.getType()) || HAZARDOUS_MATERIALS.contains(headBlock.getType())) {
            return false;
        }

        return footBlock.isPassable() && headBlock.isPassable();
    }

    public static Location findSafeLocation(Location targetLocation, int searchRadius) {
        if (isSafeLocation(targetLocation)) {
            return targetLocation;
        }

        World world = targetLocation.getWorld();
        if (world == null) {
            return null;
        }

        int startX = targetLocation.getBlockX();
        int startY = targetLocation.getBlockY();
        int startZ = targetLocation.getBlockZ();

        for (int r = 1; r <= searchRadius; r++) {
            for (int dy = -r; dy <= r; dy++) {
                int checkY = startY + dy;
                if (checkY < world.getMinHeight() + 1 || checkY >= world.getMaxHeight() - 1) {
                    continue;
                }

                for (int dx = -r; dx <= r; dx++) {
                    for (int dz = -r; dz <= r; dz++) {
                        if (Math.abs(dx) != r && Math.abs(dz) != r && Math.abs(dy) != r) {
                            continue;
                        }

                        Location candidate = new Location(
                                world,
                                startX + dx + 0.5,
                                checkY,
                                startZ + dz + 0.5,
                                targetLocation.getYaw(),
                                targetLocation.getPitch()
                        );

                        if (isSafeLocation(candidate)) {
                            return candidate;
                        }
                    }
                }
            }
        }

        return null;
    }
}
