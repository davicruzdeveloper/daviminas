package com.davidev.daviminas.Commands;

import com.davidev.daviminas.Yaml.SaveManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SetarSpawnCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final SaveManager saveManager;

    public SetarSpawnCommand(JavaPlugin plugin, SaveManager saveManager) {
        this.plugin = plugin;
        this.saveManager = saveManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("setarspawn")) {
                Location location = player.getLocation();
                saveManager.setString("spawn.world", location.getWorld().getName());
                saveManager.setInt("spawn.x", location.getBlockX());
                saveManager.setInt("spawn.y", location.getBlockY());
                saveManager.setInt("spawn.z", location.getBlockZ());
                player.sendMessage("Spawn normal definido em: " + location.toVector().toString());
                return true;
            }
        } else {
            sender.sendMessage("Apenas jogadores podem usar este comando.");
        }
        return false;
    }
}
