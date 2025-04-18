package com.auroramc.reports.menu;

import dev.auroramc.laas.Core;
import dev.auroramc.laas.libraries.menu.PagedPlayerMenu;
import com.auroramc.reports.manager.ReportManager;
import com.auroramc.reports.player.PlayerReportCache;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MenuReportList extends PagedPlayerMenu {

    private final Map<Integer, PlayerReportCache> REPORTS = new HashMap<>();

    public MenuReportList(Player player) {
        super(player, "Lista de Reports", 6);
        List<Integer> a = Arrays.asList(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42);
        this.onlySlots(a);
        nextPage = 26;
        previousPage = 18;
        List<ItemStack> itens = new ArrayList<>();

        ItemStack itemStack = new ItemStack(Material.BARRIER);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName("§cLimpar todos os Reports");
        itemStack.setItemMeta(meta);
        this.removeSlotsWith(itemStack, 49);

        ItemStack item = new ItemStack(Material.WEB);
        ItemMeta Itemmeta = item.getItemMeta();
        Itemmeta.setDisplayName("§cLista de Reports Vazia");
        item.setItemMeta(Itemmeta);
        this.removeSlotsWith(item, 22);

        int i = 0;
        for (PlayerReportCache reportManagerBukkit : ReportManager.loadReports()) {
            itens.add(reportManagerBukkit.getIcon());
            REPORTS.put(a.get(i), reportManagerBukkit);
            this.removeSlotsWith(new ItemStack(Material.AIR), 22);
            i++;
        }

        this.setItems(itens);
        open(player);
        register(Core.getInstance());
    }

    public void cancel() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (event.getInventory().equals(this.getCurrentInventory())) {
                ItemStack itemStack = event.getCurrentItem();
                if (itemStack != null) {
                    PlayerReportCache reportManagerBukkit = REPORTS.get(event.getSlot());
                    if (reportManagerBukkit != null) {
                        if (event.getClick().isLeftClick()) {
                            reportManagerBukkit.teleportPlayer(player);
                        } else if (event.getClick().isRightClick()) {
                            ReportManager.deleteReport(reportManagerBukkit.getPlayerUUID());
                            player.sendMessage("§aReporte deletado com sucesso!");
                        }
                    } else {
                        if (event.getSlot() == 49) {
                            ReportManager.deleteAllReports();
                            player.sendMessage("§aLista de reports limpa com sucesso!");
                        }
                    }

                    event.setCancelled(true);
                    new MenuReportList(player);
                }
            }
        }
    }
    @EventHandler
    public void onPlayerQuitListeners(PlayerQuitEvent event) {
        if (event.getPlayer().getOpenInventory().getTopInventory().equals(this.getCurrentInventory())) {
            cancel();
        }
    }

    @EventHandler
    public void onPlayerCloseInventoryListeners(InventoryCloseEvent event) {
        if (event.getInventory().equals(this.getCurrentInventory())) {
            cancel();
        }
    }
}
