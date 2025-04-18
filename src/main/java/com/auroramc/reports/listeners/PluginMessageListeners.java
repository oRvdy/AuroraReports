package com.auroramc.reports.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import dev.auroramc.laas.player.Profile;
import com.auroramc.reports.menu.MenuReportList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class PluginMessageListeners implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player1, byte[] bytes) {
        if (channel.equals("AuroraReports")) {
            ByteArrayDataInput input = ByteStreams.newDataInput(bytes);
            String subChannel = input.readUTF();
            switch (subChannel) {
                case "menu": {
                    Player player = Bukkit.getPlayer(input.readUTF());
                    Profile profile = Profile.getProfile(player.getName());
                    if (profile != null) {
                        new MenuReportList(player);
                    }
                }
            }
        }
    }

}
