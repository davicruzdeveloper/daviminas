package com.davidev.daviminas.Listener;

import com.davidev.daviminas.Commands.SetarCommand;
import com.davidev.daviminas.Yaml.SaveManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PositionSelectionListener implements Listener {
    private final JavaPlugin plugin;
    private final SaveManager saveManager;
    private SetarCommand setarCommand;

    public PositionSelectionListener(JavaPlugin plugin, SaveManager saveManager, SetarCommand setarCommand) {
        this.plugin = plugin;
        this.saveManager = saveManager;
        this.setarCommand = setarCommand;
    }

    public void setSetarCommand(SetarCommand setarCommand) {
        this.setarCommand = setarCommand;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("davi.minas.admin")) {
            if (setarCommand != null && setarCommand.getSetado()) {
                if (event.getItem() != null && event.getItem().getType() == Material.STICK) {
                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        Location pos1 = event.getClickedBlock().getLocation();
                        player.sendMessage("Posição 1 definida em: " + pos1.toVector().toString());
                        saveManager.getSaveConfig().set("mina.1.world", pos1.getWorld().getName());
                        saveManager.getSaveConfig().set("mina.1.x", pos1.getBlockX());
                        saveManager.getSaveConfig().set("mina.1.y", pos1.getBlockY());
                        saveManager.getSaveConfig().set("mina.1.z", pos1.getBlockZ());
                        saveManager.saveSaveConfig();
                    } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        Location pos2 = event.getClickedBlock().getLocation();
                        player.sendMessage("Posição 2 definida em: " + pos2.toVector().toString());
                        saveManager.getSaveConfig().set("mina.2.world", pos2.getWorld().getName());
                        saveManager.getSaveConfig().set("mina.2.x", pos2.getBlockX());
                        saveManager.getSaveConfig().set("mina.2.y", pos2.getBlockY());
                        saveManager.getSaveConfig().set("mina.2.z", pos2.getBlockZ());
                        saveManager.saveSaveConfig();
                    }
                } else if (event.getItem() != null && event.getItem().getType() == Material.EMERALD && event.getAction() == Action.RIGHT_CLICK_AIR) {
                    String pos1World = saveManager.getSaveConfig().getString("mina.1.world");
                    int pos1X = saveManager.getSaveConfig().getInt("mina.1.x");
                    int pos1Y = saveManager.getSaveConfig().getInt("mina.1.y");
                    int pos1Z = saveManager.getSaveConfig().getInt("mina.1.z");

                    String pos2World = saveManager.getSaveConfig().getString("mina.2.world");
                    int pos2X = saveManager.getSaveConfig().getInt("mina.2.x");
                    int pos2Y = saveManager.getSaveConfig().getInt("mina.2.y");
                    int pos2Z = saveManager.getSaveConfig().getInt("mina.2.z");

                    if (pos1World != null && pos2World != null) {
                        Location pos1 = new Location(Bukkit.getWorld(pos1World), pos1X, pos1Y, pos1Z);
                        Location pos2 = new Location(Bukkit.getWorld(pos2World), pos2X, pos2Y, pos2Z);
                        player.sendMessage("Posições confirmadas:\nPosição 1: " + pos1.toVector().toString() + "\nPosição 2: " + pos2.toVector().toString());
                        saveManager.getSaveConfig().set("mina.world", pos1World); // Salva o mundo onde as posições foram definidas
                        setarCommand.setSetado(false);  // Reseta a variável setado
                        saveManager.saveSaveConfig();
                        player.getInventory().clear();
                    } else {
                        player.sendMessage("Por favor, defina ambas as posições primeiro.");
                    }
                }
            }
        }
    }
}
