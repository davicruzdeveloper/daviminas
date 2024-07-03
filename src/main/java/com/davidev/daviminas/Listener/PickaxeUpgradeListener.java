package com.davidev.daviminas.Listener;

import com.davidev.daviminas.Yaml.SaveManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class PickaxeUpgradeListener implements Listener {
    private final JavaPlugin plugin;
    private final Economy economy;
    private final SaveManager saveManager;

    public PickaxeUpgradeListener(JavaPlugin plugin, Economy economy, SaveManager saveManager) {
        this.plugin = plugin;
        this.economy = economy;
        this.saveManager = saveManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();

        if (item != null && item.getType().toString().contains("PICKAXE") && event.getAction().toString().contains("RIGHT_CLICK")) {
            openUpgradeMenu(player);
        }
    }

    private void openUpgradeMenu(Player player) {
        Inventory upgradeMenu = Bukkit.createInventory(null, 27, "§aUpgrade de Picareta");

        double coinMultiplierPrice = plugin.getConfig().getDouble("upgrades.coinMultiplier.price");
        double coinChancePrice = plugin.getConfig().getDouble("upgrades.coinChance.price");

        ItemStack coinMultiplierUpgrade = createUpgradeItem(Material.GOLD_INGOT, "Multiplicador de Coins", coinMultiplierPrice);
        ItemStack coinChanceUpgrade = createUpgradeItem(Material.DIAMOND, "Chance de Coins", coinChancePrice);

        upgradeMenu.setItem(11, coinMultiplierUpgrade);
        upgradeMenu.setItem(15, coinChanceUpgrade);

        player.openInventory(upgradeMenu);
    }

    private ItemStack createUpgradeItem(Material material, String name, double cost) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList("§7Upgrade", "§aPreço: " + cost + " coins"));
            item.setItemMeta(meta);
        }
        return item;
    }
}
