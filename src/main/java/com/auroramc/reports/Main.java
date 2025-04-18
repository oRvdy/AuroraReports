package com.auroramc.reports;

import com.auroramc.reports.database.databases.Redis;
import com.auroramc.reports.listeners.PluginMessageListeners;
import com.auroramc.reports.manager.ReportManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "AuroraReports");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "AuroraReports", new PluginMessageListeners());
        instance = this;
        sendMessage("Carregando reports...", 'e');
    }

    @Override
    public void onEnable() {
        //Load DBs
        sendMessage("Carregando database...", 'e');
        Redis.setupRedisConnection(false);

        //Load funcionalitys
        ReportManager.updateReports();

        sendMessage("O plugin iniciou com sucesso!");
    }

    @Override
    public void onDisable() {
        sendMessage("O plugin desligou com sucesso!");
    }

    public void sendMessage(String message) {
        Bukkit.getConsoleSender().sendMessage("§a[" + this.getDescription().getName() + "] " + message);
    }

    public void sendMessage(String message, char color) {
        Bukkit.getConsoleSender().sendMessage("§" + color + "[" + this.getDescription().getName() + "] " + message);
    }
}
