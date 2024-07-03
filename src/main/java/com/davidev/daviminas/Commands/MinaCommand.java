package com.davidev.daviminas.Commands;

import com.davidev.daviminas.Yaml.SaveManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class MinaCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final SaveManager saveManager;
    private final Economy economy;
    private final int MINE_RADIUS = 350;  // Defina o raio para considerar "próximo" da mina
    private final int SPAWN_RADIUS = 350; // Defina o raio para considerar "próximo" do spawn normal

    public MinaCommand(JavaPlugin plugin, SaveManager saveManager, Economy economy) {
        this.plugin = plugin;
        this.saveManager = saveManager;
        this.economy = economy;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("mina")) {
                if (isInMineWorld(player)) {
                    if (isFarFromMineSpawn(player)) {
                        teleportToMineSpawn(player);
                    } else {
                        teleportToNormalSpawn(player);
                    }
                } else {
                    if (isFarFromNormalSpawn(player)) {
                        teleportToNormalSpawn(player);
                    } else {
                        teleportToMineSpawn(player);
                    }
                }
                return true;
            }
        } else {
            sender.sendMessage("Apenas jogadores podem usar este comando.");
        }
        return false;
    }

    private boolean isInMineWorld(Player player) {
        String mineWorld = saveManager.getString("mina.world");
        return player.getWorld().getName().equals(mineWorld);
    }

    private boolean isFarFromMineSpawn(Player player) {
        Location mineSpawn = getMineSpawn();
        return mineSpawn != null && player.getLocation().distance(mineSpawn) > MINE_RADIUS;
    }

    private boolean isFarFromNormalSpawn(Player player) {
        Location normalSpawn = getNormalSpawn();
        return normalSpawn != null && player.getLocation().distance(normalSpawn) > SPAWN_RADIUS;
    }

    private Location getMineSpawn() {
        String worldName = saveManager.getString("mina.spawn.world");
        int x = saveManager.getInt("mina.spawn.x");
        int y = saveManager.getInt("mina.spawn.y");
        int z = saveManager.getInt("mina.spawn.z");

        if (worldName != null) {
            return new Location(Bukkit.getWorld(worldName), x, y, z);
        } else {
            return null;
        }
    }

    private Location getNormalSpawn() {
        String worldName = saveManager.getString("spawn.world");
        int x = saveManager.getInt("spawn.x");
        int y = saveManager.getInt("spawn.y");
        int z = saveManager.getInt("spawn.z");

        if (worldName != null) {
            return new Location(Bukkit.getWorld(worldName), x, y, z);
        } else {
            return null;
        }
    }

    private void teleportToMineSpawn(Player player) {
        Location mineSpawn = getMineSpawn();
        if (mineSpawn != null) {
            clearPlayerInventory(player);
            player.teleport(mineSpawn);
            player.sendMessage("§bVocê foi teletransportado para o spawn da mina.");
            giveSpecialPickaxe(player);
        } else {
            player.sendMessage("O spawn da mina não está definido.");
        }
    }

    private void teleportToNormalSpawn(Player player) {
        Location normalSpawn = getNormalSpawn();
        if (normalSpawn != null) {
            restorePlayerInventory(player);
            player.teleport(normalSpawn);
            player.sendMessage("§bVocê foi teletransportado para o spawn normal.");
        } else {
            player.sendMessage("O spawn normal não está definido.");
        }
    }

    private void giveSpecialPickaxe(Player player) {
        ItemStack specialPickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta meta = specialPickaxe.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6Picareta Especial");
            specialPickaxe.setItemMeta(meta);
        }
        player.getInventory().addItem(specialPickaxe);
        player.sendMessage("§cVocê recebeu uma picareta especial!");
    }

    private void clearPlayerInventory(Player player) {
        savePlayerInventory(player);
        player.getInventory().clear();
    }

    private void savePlayerInventory(Player player) {
        ItemStack[] inventoryContents = player.getInventory().getContents();
        for (int i = 0; i < inventoryContents.length; i++) {
            saveManager.setString(player.getUniqueId() + ".inventory.slot" + i, saveManager.serializeItemStack(inventoryContents[i]));
        }
        saveManager.saveSaveConfig();
    }

    private void restorePlayerInventory(Player player) {
        ItemStack[] inventoryContents = new ItemStack[player.getInventory().getSize()];
        for (int i = 0; i < inventoryContents.length; i++) {
            String itemData = saveManager.getString(player.getUniqueId() + ".inventory.slot" + i);
            inventoryContents[i] = saveManager.deserializeItemStack(itemData);
        }
        player.getInventory().setContents(inventoryContents);
        saveManager.clearPlayerInventoryData(player.getUniqueId().toString());
    }
}
