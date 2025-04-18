package com.auroramc.reports.manager;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.auroramc.reports.Main;
import com.auroramc.reports.database.databases.Redis;
import com.auroramc.reports.player.PlayerReportCache;
import org.bukkit.Bukkit;

import java.util.*;

public class ReportManager {

    private static final LinkedList<PlayerReportCache> REPORTS_CACHE = new LinkedList<>();

    @Deprecated
    public static void updateReports() {
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(Main.getInstance(), () -> {
            if (Bukkit.getOnlinePlayers().size() < 1) {
                return;
            }

            Redis redis = Redis.getInstance();
            String reports = redis.createConnection().get("reports");
            if (Objects.equals(reports, "") || Objects.equals(reports, "[]")) {
                REPORTS_CACHE.clear();
                return;
            }

            reports = reports.substring(1, reports.length() - 1);
            String[] elements = reports.split(", ");
            REPORTS_CACHE.clear();

            for (String settings : elements) {
                UUID playerUUID = UUID.fromString(settings.split(" ; ")[0].replace("[", ""));
                UUID reporterUUID = UUID.fromString(settings.split(" ; ")[1]);
                String date = settings.split(" ; ")[2];
                String reason = settings.split(" ; ")[3];
                Long totalReports = Long.valueOf(settings.split(" ; ")[4]);
                String lastViewer = settings.split(" ; ")[5].replace("]", "");

                PlayerReportCache pReportCache = new PlayerReportCache(playerUUID);
                pReportCache.setReporterUUID(reporterUUID);
                pReportCache.setDate(date);
                pReportCache.setReason(reason);
                pReportCache.setTotalReports(totalReports);
                pReportCache.setLastViewer(lastViewer);
                REPORTS_CACHE.add(pReportCache);
            }


        }, 20 * 2L, 20L);
    }

    public static PlayerReportCache findByUUID(UUID playerUUID) {
        return REPORTS_CACHE.stream().filter(playerReportCache -> playerReportCache.getPlayerUUID().equals(playerUUID)).findFirst().orElse(null);
    }

    public static void deleteReport(UUID playerUUID) {
        REPORTS_CACHE.remove(findByUUID(playerUUID));
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("delete");
        output.writeUTF(playerUUID.toString());
        Bukkit.getServer().sendPluginMessage(Main.getInstance(), "AuroraReports", output.toByteArray());
    }

    public static void deleteAllReports() {
        REPORTS_CACHE.clear();
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("delete");
        output.writeUTF("all");
        Bukkit.getServer().sendPluginMessage(Main.getInstance(), "AuroraReports", output.toByteArray());
    }

    public static LinkedList<PlayerReportCache> loadReports() {
        return REPORTS_CACHE;
    }
}
