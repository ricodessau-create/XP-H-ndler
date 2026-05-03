package de.bierxp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class XPHandler implements Listener {

    private final BierXP plugin;
    [span_16](start_span)private final String guiTitle = ChatColor.DARK_GREEN + "BierXP Bank";[span_16](end_span)

    public XPHandler(BierXP plugin) {
        [span_17](start_span)this.plugin = plugin;[span_17](end_span)
    }

    public void openBankGUI(Player player) {
        [span_18](start_span)Inventory inv = Bukkit.createInventory(null, 9, guiTitle);[span_18](end_span)
        
        [span_19](start_span)// Stand aus der Config laden (überlebt Neustarts)[span_19](end_span)
        int balance = plugin.getConfig().getInt("players." + player.getUniqueId(), 0);

        [span_20](start_span)inv.setItem(2, createItem(Material.ENCHANTED_BOOK, "§a10 XP Einzahlen"));[span_20](end_span)
        [span_21](start_span)inv.setItem(4, createItem(Material.PAPER, "§eKontostand: §f" + balance + " XP"));[span_21](end_span)
        [span_22](start_span)inv.setItem(6, createItem(Material.EXPERIENCE_BOTTLE, "§610 XP Auszahlen"));[span_22](end_span)

        [span_23](start_span)player.openInventory(inv);[span_23](end_span)
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        [span_24](start_span)if (!e.getView().getTitle().equals(guiTitle)) return;[span_24](end_span)
        [span_25](start_span)e.setCancelled(true);[span_25](end_span)

        [span_26](start_span)if (!(e.getWhoClicked() instanceof Player player)) return;[span_26](end_span)
        ItemStack item = e.getCurrentItem();
        [span_27](start_span)if (item == null || !item.hasItemMeta()) return;[span_27](end_span)

        UUID uuid = player.getUniqueId();
        int currentBalance = plugin.getConfig().getInt("players." + uuid, 0);

        // EINZAHLEN
        if (item.getType() == Material.ENCHANTED_BOOK) {
            int totalXP = getTotalExperience(player);
            if (totalXP >= 10) {
                setTotalExperience(player, totalXP - 10); [span_28](start_span)// Präzises Abziehen[span_28](end_span)
                plugin.getConfig().set("players." + uuid, currentBalance + 10);
                plugin.saveConfig();
                [span_29](start_span)player.sendMessage("§a10 XP eingezahlt.");[span_29](end_span)
                openBankGUI(player);
            } else {
                [span_30](start_span)player.sendMessage("§cDu hast nicht genug XP!");[span_30](end_span)
            }
        }

        // AUSZAHLEN
        if (item.getType() == Material.EXPERIENCE_BOTTLE) {
            if (currentBalance >= 10) {
                plugin.getConfig().set("players." + uuid, currentBalance - 10);
                plugin.saveConfig();
                [span_31](start_span)player.giveExp(10);[span_31](end_span)
                [span_32](start_span)player.sendMessage("§610 XP ausgezahlt.");[span_32](end_span)
                [span_33](start_span)player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);[span_33](end_span)
                openBankGUI(player);
            } else {
                [span_34](start_span)player.sendMessage("§cBank leer! Kein Dupen möglich.");[span_34](end_span)
            }
        }
    }

    // Hilfsmethoden für absolut präzise XP-Handhabung
    private int getTotalExperience(Player player) {
        int exp = Math.round(player.getExp() * player.expToLevel());
        int level = player.getLevel();
        while (level > 0) {
            level--;
            exp += getExpAtLevel(level);
        }
        return exp;
    }

    private void setTotalExperience(Player player, int exp) {
        player.setExp(0);
        player.setLevel(0);
        player.setTotalExperience(0);
        player.giveExp(exp);
    }

    private int getExpAtLevel(int level) {
        if (level <= 15) return (2 * level) + 7;
        if (level <= 30) return (5 * level) - 38;
        return (9 * level) - 158;
    }

    private ItemStack createItem(Material mat, String name) {
        [span_35](start_span)ItemStack item = new ItemStack(mat);[span_35](end_span)
        [span_36](start_span)ItemMeta meta = item.getItemMeta();[span_36](end_span)
        [span_37](start_span)meta.setDisplayName(name);[span_37](end_span)
        [span_38](start_span)item.setItemMeta(meta);[span_38](end_span)
        return item;
    }
                                                                           }
