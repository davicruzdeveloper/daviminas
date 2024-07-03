package com.davidev.daviminas;

import com.davidev.daviminas.Commands.MinaCommand;
import com.davidev.daviminas.Commands.SetarCommand;
import com.davidev.daviminas.Commands.SetarSpawnCommand;
import com.davidev.daviminas.Listener.BlockBreakListener;
import com.davidev.daviminas.Listener.PickaxeUpgradeListener;
import com.davidev.daviminas.Listener.PositionSelectionListener;
import com.davidev.daviminas.Listener.RegenerationListener;
import com.davidev.daviminas.Listener.UpgradeListener;
import com.davidev.daviminas.Yaml.SaveManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public final class Main extends JavaPlugin {
    private SaveManager saveManager;
    private SetarCommand setarCommand;
    private Economy economy;

    @Override
    public void onEnable() {
        // Salvar o config.yml padrão se não existir
        saveDefaultConfig();

        // Criar o save.yml a partir dos recursos se não existir
        createSaveYml();

        // Inicializa o SaveManager
        saveManager = new SaveManager(this);

        // Configura a economia
        if (!setupEconomy()) {
            getLogger().severe("Vault não encontrado! Desativando plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Inicializa o SetarCommand
        setarCommand = new SetarCommand(this, saveManager);
        PositionSelectionListener positionSelectionListener = new PositionSelectionListener(this, saveManager, setarCommand);

        this.getCommand("setarspawn").setExecutor(new SetarSpawnCommand(this, saveManager));
        this.getCommand("mina").setExecutor(new MinaCommand(this, saveManager, economy));

        // Registra o comando de setar
        this.getCommand("setar").setExecutor(setarCommand);

        // Registra os listeners
        getServer().getPluginManager().registerEvents(positionSelectionListener, this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this, economy, saveManager), this);
        getServer().getPluginManager().registerEvents(new PickaxeUpgradeListener(this, economy, saveManager), this);
        getServer().getPluginManager().registerEvents(new UpgradeListener(this, economy, saveManager), this);

        // Aguarda alguns segundos para garantir que o Vault tenha tempo de carregar
        new BukkitRunnable() {
            @Override
            public void run() {
                if (setupEconomy()) {
                    // Regenera a mina ao iniciar o servidor e agenda regeneração a cada 120 segundos
                    new RegenerationListener(Main.this, saveManager).runTaskTimer(Main.this, 0, 2400);
                } else {
                    getLogger().severe("Vault não encontrado! Desativando plugin.");
                    getServer().getPluginManager().disablePlugin(Main.this);
                }
            }
        }.runTaskLater(this, 20L * 5); // Atraso de 5 segundos
    }

    @Override
    public void onDisable() {
        // Lógica de desligamento, se necessário
    }

    private void createSaveYml() {
        File saveFile = new File(getDataFolder(), "save.yml");
        if (!saveFile.exists()) {
            try (InputStream in = getResource("save.yml")) {
                if (in != null) {
                    Files.copy(in, saveFile.toPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe("Vault não encontrado! Desativando plugin.");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().severe("Serviço de economia não encontrado! Desativando plugin.");
            return false;
        }
        economy = rsp.getProvider();
        getLogger().info("Economia configurada com sucesso!");
        return economy != null;
    }
}
