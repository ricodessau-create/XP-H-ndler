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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class XPHandler implements Listener {

    private final BierXP plugin;
    private final String guiTitle = ChatColor.DARK_GREEN + "BierXP Bank";
    
    // DER FIX: Hier wird das tatsächliche Guthaben gespeichert.
    // In einer produktiven Umgebung sollte dies in einer Datenbank/Config gespeichert werden.
    private final Map<UUID, Integer> bankBalances = new HashMap<>();

    public XPHandler(BierXP plugin) {
        this.plugin = plugin;
    }

    public void openBankGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, guiTitle);

        ItemStack deposit = createItem(Material.ENCHANTED_BOOK, "§a10 XP Einzahlen");
        ItemStack withdraw = createItem(Material.EXPERIENCE_BOTTLE, "§610 XP Auszahlen");
        ItemStack info = createItem(Material.PAPER, "§eDein Kontostand: §f" + bankBalances.getOrDefault(player.getUniqueId(), 0) + " XP");

        inv.setItem(2, deposit);
        inv.setItem(4, info);
        inv.setItem(6, withdraw);

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
        int currentBalance = bankBalances.getOrDefault(uuid, 0);

        // EINZAHLEN
        if (item.getType() == Material.ENCHANTED_BOOK) {
            if (player.getTotalExperience() >= 10) {
                // XP vom Spieler abziehen (vereinfacht)
                player.setTotalExperience(0);
                player.setExp(0);
                player.setLevel(0);
                player.giveExp(player.getTotalExperience() - 10); // Nur ein Beispiel, richtige XP-Logik nutzen!

                bankBalances.put(uuid, currentBalance + 10);
                player.sendMessage("§a10 XP eingezahlt.");
                openBankGUI(player); // Update Info
            } else {
                player.sendMessage("§cDu hast nicht genug XP dabei!");
            }
        }

        // AUSZAHLEN (HIER WAR DER DUPE)
        if (item.getType() == Material.EXPERIENCE_BOTTLE) {
            // FIX: Prüfen, ob der Spieler ÜBERHAUPT Guthaben auf der Bank hat
            if (currentBalance >= 10) {
                bankBalances.put(uuid, currentBalance - 10);
                player.giveExp(10);
                player.sendMessage("§610 XP ausgezahlt.");
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                openBankGUI(player); // Update Info
            } else {
                player.sendMessage("§cDeine Bank ist leer! Du kannst nichts duupen ;)");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            }
        }
    }

    private ItemStack createItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}
