package com.auroramc.reports.player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.auroramc.laas.player.role.Role;
import com.auroramc.reports.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlayerReportCache {

    private UUID playerUUID;
    private UUID reporterUUID;
    private String lastViewer;
    private String date;
    private String reason;
    private Long totalReports;

    public PlayerReportCache(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public ItemStack getIcon() {
        ItemStack itemStack = new ItemStack(Material.matchMaterial("351"));
        itemStack.setDurability((short) 8);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(Role.getColored(Bukkit.getOfflinePlayer(this.playerUUID).getName()));
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§fAutor: " + Role.getColored(Bukkit.getOfflinePlayer(this.reporterUUID).getName()));
        lore.add("§fMotivo: §a" + this.reason);
        lore.add("§fData do ocorrido: §a" + this.date);
        lore.add("§fTotal de reports: §a" + this.totalReports);
        lore.add("§fVisualizado por: §a" + (Objects.equals(this.lastViewer.replace(" ", ""), "Ninguém")  ? "§cNinguém" : Role.getColored(this.lastViewer.replace(" ", ""))));
        lore.add("");
        lore.add("§8Ações:");
        lore.add(" §8* §7Botão esquerdo teleporta até o jogador");
        lore.add(" §8* §7Botão direito deleta este report");
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public void teleportPlayer(Player player) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("teleport");
        output.writeUTF(player.getUniqueId().toString());
        output.writeUTF(this.playerUUID.toString());
        Bukkit.getServer().sendPluginMessage(Main.getInstance(), "AuroraReports", output.toByteArray());

        player.sendMessage("§aTeleportando...");
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setReporterUUID(UUID playerUUID) {
        this.reporterUUID = playerUUID;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setTotalReports(Long totalReports) {
        this.totalReports = totalReports;
    }

    public void setLastViewer(String lastViewer) {
        this.lastViewer = lastViewer;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public String getDate() {
        return date;
    }

    public String getReason() {
        return reason;
    }

    public Long getTotalReports() {
        return totalReports;
    }

    public String getLastViewer() {
        return lastViewer.replace(" ", "");
    }

    public UUID getReporterUUID() {
        return reporterUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }
}
