package com.auroramc.reports.bungee.commands;

import com.auroramc.reports.bungee.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;

public abstract class Commands extends Command {

    public Commands(String name, String... aliases) {
        super(name, null, aliases);
        ProxyServer.getInstance().getPluginManager().registerCommand(Main.getInstance(), this);
    }

    public static void setupCommands() {
        new com.auroramc.reports.bungee.commands.ReportCommand();
        new com.auroramc.reports.bungee.commands.ReportsCommand();
    }

    public abstract void perform(CommandSender sender, String[] args);

    @Override
    public void execute(CommandSender sender, String[] args) {
        this.perform(sender, args);
    }
}
