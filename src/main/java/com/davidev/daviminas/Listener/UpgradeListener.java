// UpgradeListener.java
package com.davidev.daviminas.Listener;

import com.davidev.daviminas.Yaml.SaveManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class UpgradeListener implements Listener {
    private final JavaPlugin plugin;
    private final Economy economy;
    private final SaveManager saveManager;

    public UpgradeListener(JavaPlugin plugin, Economy economy, SaveManager saveManager) {
        this.plugin = plugin;
        this.economy = economy;
        this.saveManager = saveManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("§aUpgrade de Picareta")) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                return;
            }

            String playerUUID = player.getUniqueId().toString();
            double playerBalance = economy.getBalance(player);

            if (clickedItem.getType() == Material.GOLD_INGOT) {
                handleUpgrade(player, playerUUID, "coinMultiplier", 1000);
            } else if (clickedItem.getType() == Material.DIAMOND) {
                handleUpgrade(player, playerUUID, "coinChance", 2000);
            }
        }
    }

    private void handleUpgrade(Player player, String playerUUID, String upgradeType, double baseCost) {
        int level = saveManager.getUpgradeLevel(playerUUID, upgradeType);
        double cost = baseCost * (level + 1);

        if (economy.getBalance(player) >= cost) {
            economy.withdrawPlayer(player, cost);
            saveManager.setUpgradeLevel(playerUUID, upgradeType, level + 1);
            player.sendMessage("§a Upgrade de " + upgradeType + " para nível " + (level + 1) + " por " + cost + " coins.");
        } else {
            player.sendMessage("§c Você não tem coins suficientes para esse upgrade.");
        }
    }
}