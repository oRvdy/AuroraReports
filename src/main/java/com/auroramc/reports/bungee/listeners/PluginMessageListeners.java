package com.auroramc.reports.bungee.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import dev.auroramc.laas.bungee.Bungee;
import dev.auroramc.laas.player.role.Role;
import com.auroramc.reports.bungee.Main;
import com.auroramc.reports.bungee.manager.ReportsManager;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class PluginMessageListeners implements Listener {

    @EventHandler
    public void onPluginMessageRecive(PluginMessageEvent event) {
        String channel = event.getTag();
        if (channel.equals("AuroraReports")) {
            ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());
            String subChannel = input.readUTF();
            switch (subChannel) {
                case "delete": {
                    String type = input.readUTF();
                    if (type.equals("all")) {
                        ReportsManager.clearReports();
                    } else {
                        UUID uuid = UUID.fromString(type);
                        ReportsManager.deleteReport(uuid);
                    }
                    break;
                }

                case "teleport": {
                    UUID uuidP = UUID.fromString(input.readUTF());
                    UUID uuidT = UUID.fromString(input.readUTF());

                    ProxiedPlayer p = Main.getInstance().getProxy().getPlayer(uuidP);
                    ProxiedPlayer pt = Main.getInstance().getProxy().getPlayer(uuidT);

                    if (pt == null) {
                        p.sendMessage(TextComponent.fromLegacyText("§cEsse usuario não se encontra online no momento!"));
                        return;
                    }

                    if (ReportsManager.findReportSettings(pt.getUniqueId()) != null) {
                        if (p.getServer().equals(pt.getServer())) {
                            p.sendMessage(TextComponent.fromLegacyText("§cEsse usuario já se encontra no mesmo servidor que você!"));
                            return;
                        }

                        if (ReportsManager.findReportSettings(pt.getUniqueId()).split(" ; ")[5].replace("]", "").equals("Ninguém")) {
                            String settings = ReportsManager.findReportSettings(pt.getUniqueId());
                            UUID reporterUUID = UUID.fromString(settings.split(" ; ")[1]);
                            String date = settings.split(" ; ")[2];
                            String reason = settings.split(" ; ")[3];
                            Long totalReports = Long.valueOf(settings.split(" ; ")[4]);
                            ReportsManager.createReport(pt.getUniqueId(), reporterUUID, reason, date, String.valueOf(totalReports), p.getName());
                        }

                        p.connect(pt.getServer().getInfo());
                        return;
                    }

                    p.sendMessage(TextComponent.fromLegacyText("§cEsse usuario não possui um report computado em nosso sistema!"));
                    break;
                }
            }
        }
    }
}
