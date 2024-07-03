package com.davidev.daviminas.Yaml;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class SaveManager {
    private final JavaPlugin plugin;
    private FileConfiguration saveConfig;
    private File saveFile;

    public SaveManager(JavaPlugin plugin) {
        this.plugin = plugin;
        saveDefaultSaveConfig();
        reloadSaveConfig();
    }

    public void reloadSaveConfig() {
        if (saveFile == null) {
            saveFile = new File(plugin.getDataFolder(), "save.yml");
        }
        saveConfig = YamlConfiguration.loadConfiguration(saveFile);
    }

    public FileConfiguration getSaveConfig() {
        if (saveConfig == null) {
            reloadSaveConfig();
        }
        return saveConfig;
    }

    public void saveSaveConfig() {
        if (saveConfig == null || saveFile == null) {
            return;
        }
        try {
            getSaveConfig().save(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDefaultSaveConfig() {
        if (saveFile == null) {
            saveFile = new File(plugin.getDataFolder(), "save.yml");
        }
        if (!saveFile.exists()) {
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getString(String path) {
        return getSaveConfig().getString(path);
    }

    public int getInt(String path) {
        return getSaveConfig().getInt(path);
    }

    public boolean getBoolean(String path) {
        return getSaveConfig().getBoolean(path);
    }

    public void setString(String path, String value) {
        getSaveConfig().set(path, value);
        saveSaveConfig();
    }

    public void setInt(String path, int value) {
        getSaveConfig().set(path, value);
        saveSaveConfig();
    }

    public void setBoolean(String path, boolean value) {
        getSaveConfig().set(path, value);
        saveSaveConfig();
    }

    public String serializeItemStack(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(byteArrayOutputStream);
            bukkitObjectOutputStream.writeObject(itemStack);
            bukkitObjectOutputStream.close();
            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível serializar o ItemStack", e);
        }
    }

    public ItemStack deserializeItemStack(String data) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(byteArrayInputStream);
            return (ItemStack) bukkitObjectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Não foi possível desserializar o ItemStack", e);
        }
    }

    public int getUpgradeLevel(String playerUUID, String upgradeType) {
        return getSaveConfig().getInt("upgrades." + playerUUID + "." + upgradeType, 0);
    }

    public void setUpgradeLevel(String playerUUID, String upgradeType, int level) {
        getSaveConfig().set("upgrades." + playerUUID + "." + upgradeType, level);
        saveSaveConfig();
    }

    public void clearPlayerInventoryData(String playerUUID) {
        getSaveConfig().set(playerUUID + ".inventory", null);
        saveSaveConfig();
    }
}
