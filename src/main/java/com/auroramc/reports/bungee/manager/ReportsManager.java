package com.auroramc.reports.bungee.manager;

import com.auroramc.reports.bungee.Main;
import com.auroramc.reports.database.DataBase;
import com.auroramc.reports.database.databases.MySQL;
import com.auroramc.reports.database.databases.Redis;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.text.SimpleDateFormat;
import java.util.*;

public class ReportsManager {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yy 'às' HH:mm:ss");
    private static final List<String> REPORTS_SETTINGS_CACHE = new ArrayList<>();

    static {
        SDF.setTimeZone(TimeZone.getTimeZone("GMT-3"));
    }

    public static void syncReportsToDB() {
        MySQL mySQL = DataBase.getDatabase(MySQL.class);
        if (mySQL != null) {
            for (String reportSettings : REPORTS_SETTINGS_CACHE) {
                String[] reportConfig = reportSettings.split(";");
                if (!mySQL.conteinsPlayer(UUID.fromString(reportConfig[0].replace(" ", "")), "ProfileReports")) {
                    mySQL.addStatusDefaultPlayer(UUID.fromString(reportConfig[0].replace(" ", "")), "ProfileReports");
                }

                mySQL.updateStatusPlayer(reportConfig[0], "ProfileReports", "REPORTER", reportConfig[1]);
                mySQL.updateStatusPlayer(reportConfig[0], "ProfileReports", "REASON", reportConfig[3]);
                mySQL.updateStatusPlayer(reportConfig[0], "ProfileReports", "DATE", reportConfig[2]);
                mySQL.updateStatusPlayer(reportConfig[0], "ProfileReports", "TOTAL", Long.parseLong(reportConfig[4].replace(" ", "")));
                mySQL.updateStatusPlayer(reportConfig[0], "ProfileReports", "LASTVIEWER", reportConfig[5]);
            }
        }
    }

    public static void setupReports() {
        MySQL mySQL = DataBase.getDatabase(MySQL.class);
        Redis.getInstance().createConnection().set("reports", "[]");

        if (mySQL != null) {
            for (UUID uuid : mySQL.loadAccountReports("ProfileReports")) {
                UUID reporter = UUID.fromString(mySQL.getStatusForPlayerString(uuid.toString(),"REPORTER", "ProfileReports").replace(" ", ""));
                String reason = mySQL.getStatusForPlayerString(uuid.toString(),"REASON", "ProfileReports");
                String date = mySQL.getStatusForPlayerString(uuid.toString(),"DATE", "ProfileReports");
                Long totalReports = mySQL.getStatusForPlayerLong(uuid.toString(),"TOTAL", "ProfileReports");
                String lastViwer = mySQL.getStatusForPlayerString(uuid.toString(),"LASTVIEWER", "ProfileReports");

                createReport(uuid, reporter, reason, date, totalReports.toString(), lastViwer);
            }
        }
    }

    public static void createReport(UUID target, UUID reporter, String reason, String date, String totalReports, String lastViwer) {
        if (findReportSettings(target) != null) {
            REPORTS_SETTINGS_CACHE.remove(findReportSettings(target));
        }

        String reportSettings = target.toString() + " ; " + reporter.toString() + " ; " + date + " ; " + reason + " ; " + totalReports + " ; " + lastViwer;
        REPORTS_SETTINGS_CACHE.add(reportSettings);

        Redis redis = Redis.getInstance();
        redis.createConnection().set("reports", REPORTS_SETTINGS_CACHE.toString());
    }

    public static void createReport(UUID target, UUID reporter, String reason) {
        String reportSettings;
        if (findReportSettings(target) != null) {
            long total = getTotalReports(findReportSettings(target)) + 1;
            reportSettings = target.toString() + " ; " + reporter.toString() + " ; " + SDF.format(new Date()) + " ; " + reason + " ; " + total + " ; " + findReportSettings(target).split(" ; ")[5].replace("]", "");
            deleteReport(target);
        } else {
            reportSettings = target.toString() + " ; " + reporter.toString() + " ; " + SDF.format(new Date()) + " ; " + reason + " ; " + "1" + " ; " + "Ninguém";
        }

        REPORTS_SETTINGS_CACHE.add(reportSettings);

        Redis redis = Redis.getInstance();
        redis.createConnection().set("reports", REPORTS_SETTINGS_CACHE.toString());

        sendMessageForStaffers(target, reporter, reason);
    }

    public static void deleteReport(UUID target) {
        String reportSettings = findReportSettings(target);
        REPORTS_SETTINGS_CACHE.remove(reportSettings);

        Redis redis = Redis.getInstance();
        redis.createConnection().set("reports", REPORTS_SETTINGS_CACHE.toString());

        new Thread(()-> {
            MySQL mySQL = DataBase.getDatabase(MySQL.class);
            if (mySQL != null) {
                if (mySQL.conteinsPlayer(target, "ProfileReports")) {
                    mySQL.deleteColumn("ProfileReports", "UUID", target.toString());
                }
            }
        }).start();
    }

    public static String findReportSettings(UUID target) {
        return REPORTS_SETTINGS_CACHE.stream().filter(s -> s.split(" ; ")[0].equals(target.toString())).findFirst().orElse(null);
    }

    public static Long getTotalReports(String reportConfiguration) {
        return Long.parseLong(reportConfiguration.split(" ; ")[4]);
    }

    public static void sendMessageForStaffers(UUID target, UUID reporter, String reason) {
        TextComponent text = new TextComponent("\n§e§lNOVO REPORT\n " +
                "\n§fAlvo: §a" + Main.getInstance().getProxy().getPlayer(target).getName() +
                "\n§fAuthor: §a" + Main.getInstance().getProxy().getPlayer(reporter).getName() +
                "\n§fMotivo: §a" + reason +
                "\n \n§eClique ");
        TextComponent textComponent = new TextComponent("AQUI");
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reports"));
        textComponent.setColor(ChatColor.GOLD);
        textComponent.setBold(true);
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Clique aqui para se teleportar até este jogador!")));
        textComponent.addExtra(" §epara se teleportar até este jogador!");
        text.addExtra(textComponent);
        Main.getInstance().getProxy().getPlayers().stream().filter(proxiedPlayer -> proxiedPlayer.hasPermission("vreports.admin.message")).forEach(proxiedPlayer -> proxiedPlayer.sendMessage(text));
    }

    public static void clearReports() {
        if (REPORTS_SETTINGS_CACHE.isEmpty()) {
            return;
        }

        for (String settings : REPORTS_SETTINGS_CACHE) {
            new Thread(()-> {
                MySQL mySQL = DataBase.getDatabase(MySQL.class);
                if (mySQL != null) {
                    if (mySQL.conteinsPlayer(UUID.fromString(settings.split(" ; ")[0]), "ProfileReports")) {
                        mySQL.deleteColumn("ProfileReports", "UUID", settings.split(" ; ")[0].replace(" ", ""));
                    }
                }
            }).start();
        }

        REPORTS_SETTINGS_CACHE.clear();
        Redis redis = Redis.getInstance();
        redis.createConnection().set("reports", REPORTS_SETTINGS_CACHE.toString());
    }
}
