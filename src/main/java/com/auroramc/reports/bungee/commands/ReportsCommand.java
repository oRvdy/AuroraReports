package com.auroramc.reports.bungee.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.auroramc.laas.bungee.cmd.Commands;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ReportsCommand extends Commands {

    public ReportsCommand() {
        super("reports");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText("§cEste comando é exlusivo para jogadores!"));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (!player.hasPermission("reportsapi.reports")) {
            player.sendMessage(TextComponent.fromLegacyText("§cSomente Ajudante ou superior podem executar este comando."));
            return;
        }

        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("menu");
        output.writeUTF(player.getName());
        player.getServer().sendData("AuroraReports", output.toByteArray());
    }
}
