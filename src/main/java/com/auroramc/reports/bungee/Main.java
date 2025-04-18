package com.auroramc.reports.bungee;

import com.auroramc.reports.bungee.commands.Commands;
import com.auroramc.reports.bungee.listeners.PluginMessageListeners;
import com.auroramc.reports.bungee.manager.ReportsManager;
import com.auroramc.reports.database.DataBase;
import com.auroramc.reports.database.DataTypes;
import com.auroramc.reports.database.databases.Redis;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main extends Plugin {

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        loadDefaultConfig("config");
        this.getProxy().registerChannel("AuroraReports");

        sendMessage("Carregando reports...", 'e');
    }

    @Override
    public void onEnable() {
        //Load DBs
        sendMessage("Carregando database...", 'e');
        DataBase.setupDataBases(DataTypes.MYSQL, this);

        //Load funcionalitys
        Commands.setupCommands();
        ReportsManager.setupReports();

        this.getProxy().getPluginManager().registerListener(this, new PluginMessageListeners());

        sendMessage("O plugin iniciou com sucesso!");
    }

    @Override
    public void onDisable() {
        sendMessage("Salvando informações na database de forma permanente...", 'e');
        ReportsManager.syncReportsToDB();

        sendMessage("O plugin desligou com sucesso!");
    }

    public void loadDefaultConfig(String archiveName) {
        File file = new File("plugins/" + this.getDescription().getName() + "/" + archiveName + ".yml");
        if (!file.exists()) {
            File folder = file.getParentFile();
            if (!folder.exists()) {
                folder.mkdirs();
            }

            try {
                Files.copy(this.getClass().getResourceAsStream("/" + archiveName + ".yml"), file.toPath());
            } catch (IOException e) {
                sendMessage("Ocorreu um erro enquanto o arquivo " + archiveName + ".yml estava dando load!", 'c');
            }
        }
    }

    public Configuration getConfig(String arquiveName) {
        try {
            return YamlConfiguration.getProvider(YamlConfiguration.class).load(new File("plugins/" + this.getDescription().getName() + "/" + arquiveName + ".yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void sendMessage(String message) {
        this.getLogger().info("§a" + message);
    }

    public void sendMessage(String message, char color) {
        this.getLogger().info("§" + color + message);
    }
}
