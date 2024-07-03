package com.davidev.daviminas.Listener;

import com.davidev.daviminas.Yaml.SaveManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

import java.util.Random;

public class BlockBreakListener implements Listener {
    private final JavaPlugin plugin;
    private final Economy economy;
    private final SaveManager saveManager;

    public BlockBreakListener(JavaPlugin plugin, Economy economy, SaveManager saveManager) {
        this.plugin = plugin;
        this.economy = economy;
        this.saveManager = saveManager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Material blockType = block.getType();

        if (economy == null) {
            plugin.getLogger().severe("Economia não configurada corretamente!");
            return;
        }

        if (isRelevantOre(blockType)) {
            event.setCancelled(true);
            block.setType(Material.AIR);

            double reward = getRewardForOre(player, blockType);
            economy.depositPlayer(player, reward);
            sendActionBar(player, "§a+" + reward + " coins");

            // Remove os itens dropados manualmente
            block.getDrops().clear();
        }
    }

    private boolean isRelevantOre(Material material) {
        return material == Material.COAL_ORE || material == Material.IRON_ORE ||
                material == Material.GOLD_ORE || material == Material.DIAMOND_ORE ||
                material == Material.EMERALD_ORE || material == Material.LAPIS_ORE ||
                material == Material.REDSTONE_ORE || material == Material.STONE;
    }

    private double getRewardForOre(Player player, Material material) {
        String materialName = material.toString();
        double baseReward = plugin.getConfig().getDouble("ore-rewards." + materialName, 0);

        String playerUUID = player.getUniqueId().toString();
        double coinMultiplier = saveManager.getSaveConfig().getDouble("upgrades." + playerUUID + ".coinMultiplier", 1.0);
        double coinChance = saveManager.getSaveConfig().getDouble("upgrades." + playerUUID + ".coinChance", 0.0);

        Random random = new Random();
        if (random.nextDouble() * 100 <= coinChance) {
            return baseReward * coinMultiplier;
        }
        return baseReward;
    }

    private void sendActionBar(Player player, String message) {
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
