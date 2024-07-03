package com.davidev.daviminas.Commands;

import com.davidev.daviminas.Listener.PositionSelectionListener;
import com.davidev.daviminas.Yaml.SaveManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class SetarCommand implements CommandExecutor {
    private boolean setado = false;
    private final JavaPlugin plugin;
    private final PositionSelectionListener positionSelectionListener;
    private final SaveManager saveManager;

    public SetarCommand(JavaPlugin plugin, SaveManager saveManager) {
        this.plugin = plugin;
        this.saveManager = saveManager;
        this.positionSelectionListener = new PositionSelectionListener(plugin, saveManager, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("setar")) {
                if (player.hasPermission("davi.minas.admin")) {
                    if (args.length < 1) {
                        player.sendMessage("§a Uso correto: /setar (mina/spawn)");
                        return false;
                    }
                    String argumento = args[0].toLowerCase();
                    if (argumento.equals("mina")) {
                        setado = true;
                        ItemStack stick = new ItemStack(Material.STICK, 1);
                        player.getInventory().addItem(stick);
                        ItemStack emerald = new ItemStack(Material.EMERALD, 1);
                        player.getInventory().addItem(emerald);
                        player.sendMessage("§a O STICK DEFINE ONDE VAI SPAWNAR OS MINERIOS");

                        // Chama diretamente o listener dentro do comando
                        plugin.getServer().getPluginManager().registerEvents(positionSelectionListener, plugin);
                    } else if (argumento.equals("spawn")) {
                        Location location = player.getLocation();
                        saveManager.setString("mina.spawn.world", location.getWorld().getName());
                        saveManager.setInt("mina.spawn.x", location.getBlockX());
                        saveManager.setInt("mina.spawn.y", location.getBlockY());
                        saveManager.setInt("mina.spawn.z", location.getBlockZ());
                        player.sendMessage("Spawn da mina definido em: " + location.toVector().toString());
                    }
                } else {
                    player.sendMessage("EITA...");
                }
            }
        } else {
            sender.sendMessage("Você é um console, logo não pode usar esse comando.");
        }
        return false;
    }

    public boolean getSetado() {
        return setado;
    }

    public void setSetado(boolean set2) {
        this.setado = set2;
    }
}
