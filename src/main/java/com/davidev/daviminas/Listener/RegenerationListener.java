package com.davidev.daviminas.Listener;

import com.davidev.daviminas.Yaml.SaveManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class RegenerationListener extends BukkitRunnable {
    private final JavaPlugin plugin;
    private final SaveManager saveManager;
    private final Random random = new Random();

    public RegenerationListener(JavaPlugin plugin, SaveManager saveManager) {
        this.plugin = plugin;
        this.saveManager = saveManager;
    }

    @Override
    public void run() {
        teleportPlayersOutOfMine();
        regenerateMine();
    }

    private void teleportPlayersOutOfMine() {
        String spawnWorldName = saveManager.getString("mina.spawn.world");
        int spawnX = saveManager.getInt("mina.spawn.x");
        int spawnY = saveManager.getInt("mina.spawn.y");
        int spawnZ = saveManager.getInt("mina.spawn.z");

        World spawnWorld = Bukkit.getWorld(spawnWorldName);
        if (spawnWorld == null) {
            plugin.getLogger().warning("Mundo do spawn da mina não encontrado!");
            return;
        }

        Location spawnLocation = new Location(spawnWorld, spawnX, spawnY, spawnZ);

        String worldName = saveManager.getString("mina.1.world");
        if (worldName == null) {
            plugin.getLogger().warning("Nome do mundo da mina não definido!");
            return;
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            plugin.getLogger().warning("Mundo " + worldName + " não encontrado!");
            return;
        }

        int x1 = saveManager.getInt("mina.1.x");
        int y1 = saveManager.getInt("mina.1.y");
        int z1 = saveManager.getInt("mina.1.z");

        int x2 = saveManager.getInt("mina.2.x");
        int y2 = saveManager.getInt("mina.2.y");
        int z2 = saveManager.getInt("mina.2.z");

        for (Player player : world.getPlayers()) {
            Location playerLocation = player.getLocation();
            if (isWithinMineBounds(playerLocation, x1, y1, z1, x2, y2, z2)) {
                player.teleport(spawnLocation);
            }
        }
    }

    private boolean isWithinMineBounds(Location loc, int x1, int y1, int z1, int x2, int y2, int z2) {
        return loc.getX() >= Math.min(x1, x2) && loc.getX() <= Math.max(x1, x2) &&
                loc.getY() >= Math.min(y1, y2) && loc.getY() <= Math.max(y1, y2) &&
                loc.getZ() >= Math.min(z1, z2) && loc.getZ() <= Math.max(z1, z2);
    }

    public void regenerateMine() {
        String worldName1 = saveManager.getString("mina.1.world");
        String worldName2 = saveManager.getString("mina.2.world");

        if (worldName1 == null || worldName2 == null) {
            plugin.getLogger().warning("Nome do mundo da mina não definido!");
            return;
        }

        if (!worldName1.equals(worldName2)) {
            plugin.getLogger().warning("Os mundos das posições não são iguais!");
            return;
        }

        World world = Bukkit.getWorld(worldName1);
        if (world == null) {
            plugin.getLogger().warning("Mundo " + worldName1 + " não encontrado!");
            return;
        }

        int x1 = saveManager.getInt("mina.1.x");
        int y1 = saveManager.getInt("mina.1.y");
        int z1 = saveManager.getInt("mina.1.z");

        int x2 = saveManager.getInt("mina.2.x");
        int y2 = saveManager.getInt("mina.2.y");
        int z2 = saveManager.getInt("mina.2.z");

        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
                    Location loc = new Location(world, x, y, z);
                    Material material = getRandomOre();
                    loc.getBlock().setType(material);
                }
            }
        }

        plugin.getLogger().info("Mina regenerada!");
    }

    private Material getRandomOre() {
        Material[] ores = {Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.LAPIS_ORE};
        return ores[random.nextInt(ores.length)];
    }
}
