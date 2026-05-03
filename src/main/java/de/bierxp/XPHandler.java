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
    private final String guiTitle = ChatColor.DARK_GREEN + "BierXP Bank";

    public XPHandler(BierXP plugin) {
        this.plugin = plugin;
    }

    public void openBankGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, guiTitle);
        int balance = plugin.getConfig().getInt("players." + player.getUniqueId(), 0);

        inv.setItem(2, createItem(Material.ENCHANTED_BOOK, "§a10 XP Einzahlen"));
        inv.setItem(4, createItem(Material.PAPER, "§eKontostand: §f" + balance + " XP"));
        inv.setItem(6, createItem(Material.EXPERIENCE_BOTTLE, "§610 XP Auszahlen"));

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(guiTitle)) return;
        e.setCancelled(true);

        if (!(e.getWhoClicked() instanceof Player player)) return;
        ItemStack item = e.getCurrentItem();
        if (item == null || !item.hasItemMeta()) return;

        UUID uuid = player.getUniqueId();
        int currentBalance = plugin.getConfig().getInt("players." + uuid, 0);

        if (item.getType() == Material.ENCHANTED_BOOK) {
            int totalXP = getTotalExperience(player);
            if (totalXP >= 10) {
                setTotalExperience(player, totalXP - 10);
                plugin.getConfig().set("players." + uuid, currentBalance + 10);
                plugin.saveConfig();
                player.sendMessage("§a10 XP eingezahlt.");
                openBankGUI(player);
            } else {
                player.sendMessage("§cDu hast nicht genug XP!");
            }
        }

        if (item.getType() == Material.EXPERIENCE_BOTTLE) {
            if (currentBalance >= 10) {
                plugin.getConfig().set("players." + uuid, currentBalance - 10);
                plugin.saveConfig();
                player.giveExp(10);
                player.sendMessage("§610 XP ausgezahlt.");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                openBankGUI(player);
            } else {
                player.sendMessage("§cBank leer!");
            }
        }
    }

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
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}
