package de.bierxp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class XPHandler implements Listener {

    private final BierXP plugin;
    private Map<Player, Integer> bottleCreationQueue = new HashMap<>();
    private String guiTitle = ChatColor.DARK_GREEN + "BierXP Bank";

    public XPHandler(BierXP plugin) {
        this.plugin = plugin;
    }

    // ------------------------------------------------------------
    //  NPC INTERACTION
    // ------------------------------------------------------------
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteractNPC(PlayerInteractEntityEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        Entity entity = e.getRightClicked();

        if (entity.getName().equals("BierXP Banker")) {
            e.setCancelled(true);
            openBankGUI((Player) e.getPlayer());
        }
    }

    // ------------------------------------------------------------
    //  GUI
    // ------------------------------------------------------------
    private void openBankGUI(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, guiTitle);
        int storedXp = getStoredXp(p.getUniqueId());

        ItemStack info = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName(ChatColor.GREEN + "Dein Guthaben");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + "Gespeichert: " + ChatColor.WHITE + storedXp + " XP");
        infoMeta.setLore(lore);
        info.setItemMeta(infoMeta);
        inv.setItem(13, info);

        inv.setItem(10, createButton(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "100 XP einzahlen", null));
        inv.setItem(11, createButton(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "1000 XP einzahlen", null));
        inv.setItem(12, createButton(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "Alles einzahlen", null));

        inv.setItem(14, createButton(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "100 XP auszahlen", null));
        inv.setItem(15, createButton(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "1000 XP auszahlen", null));
        inv.setItem(16, createButton(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Alles auszahlen", null));

        inv.setItem(22, createButton(Material.GLASS_BOTTLE, ChatColor.AQUA + "XP Flasche füllen", "Klick mich"));

        p.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(guiTitle)) return;
        e.setCancelled(true);

        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();

        int slot = e.getRawSlot();

        if (slot == 10) { depositXp(p, 100); openBankGUI(p); }
        if (slot == 11) { depositXp(p, 1000); openBankGUI(p); }
        if (slot == 12) { depositAllXp(p); openBankGUI(p); }

        if (slot == 14) { withdrawXp(p, 100); openBankGUI(p); }
        if (slot == 15) { withdrawXp(p, 1000); openBankGUI(p); }
        if (slot == 16) { withdrawAllXp(p); openBankGUI(p); }

        if (slot == 22) {
            p.closeInventory();
            p.sendMessage(ChatColor.AQUA + "Schreibe die Menge an XP in den Chat. (Abbruch: 'stop')");
            bottleCreationQueue.put(p, 1);
        }
    }

    // ------------------------------------------------------------
    //  CHAT INPUT FOR XP BOTTLES
    // ------------------------------------------------------------
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (bottleCreationQueue.containsKey(p)) {
            e.setCancelled(true);
            String msg = e.getMessage();

            if (msg.equalsIgnoreCase("stop")) {
                bottleCreationQueue.remove(p);
                p.sendMessage(ChatColor.RED + "Abgebrochen.");
                return;
            }

            try {
                int amount = Integer.parseInt(msg);
                if (amount <= 0) throw new NumberFormatException();

                int finalAmount = amount;
                plugin.getServer().getScheduler().runTask(plugin, () -> createXpBottle(p, finalAmount));

            } catch (NumberFormatException ex) {
                p.sendMessage(ChatColor.RED + "Ungültige Zahl.");
            }

            bottleCreationQueue.remove(p);
        }
    }

    // ------------------------------------------------------------
    //  XP BOTTLE USE
    // ------------------------------------------------------------
    @EventHandler
    public void onBottleThrow(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getHand() != EquipmentSlot.HAND) return;

        ItemStack item = e.getItem();
        if (item == null || item.getType() != Material.EXPERIENCE_BOTTLE) return;
        if (!item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        if (!meta.getDisplayName().startsWith(ChatColor.AQUA + "XP Flasche (")) return;

        List<String> lore = meta.getLore();
        if (lore == null || lore.isEmpty()) return;

        try {
            String data = ChatColor.stripColor(lore.get(0)).replace("Enthält: ", "").replace(" XP", "");
            int xp = Integer.parseInt(data);

            item.setAmount(item.getAmount() - 1);
            e.getPlayer().giveExp(xp);
            e.getPlayer().sendMessage(ChatColor.GREEN + "+" + xp + " XP erhalten!");
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

            e.setCancelled(true);
        } catch (Exception ex) { }
    }

    // ------------------------------------------------------------
    //  XP SYSTEM (DUPE-SAFE)
    // ------------------------------------------------------------

    // ECHTER XP-WERT
    private int getRealTotalXP(Player p) {
        int level = p.getLevel();
        float progress = p.getExp();

        int xpForLevel = getXpForLevel(level);
        int xpForProgress = Math.round(progress * getXpToNextLevel(level));

        return xpForLevel + xpForProgress;
    }

    private int getXpForLevel(int level) {
        if (level <= 16) return level * level + 6 * level;
        if (level <= 31) return (int) (2.5 * level * level - 40.5 * level + 360);
        return (int) (4.5 * level * level - 162.5 * level + 2220);
    }

    private int getXpToNextLevel(int level) {
        if (level <= 15) return 2 * level + 7;
        if (level <= 30) return 5 * level - 38;
        return 9 * level - 158;
    }

    // XP KORREKT ABZIEHEN
    private void removeXp(Player p, int amount) {
        int total = getRealTotalXP(p);
        int newTotal = Math.max(0, total - amount);

        p.setExp(0);
        p.setLevel(0);
        p.setTotalExperience(0);

        p.giveExp(newTotal);
    }

    // ------------------------------------------------------------
    //  BANKING
    // ------------------------------------------------------------
    private void depositXp(Player p, int amount) {
        int total = getRealTotalXP(p);
        if (total < amount) {
            p.sendMessage(ChatColor.RED + "Nicht genug XP!");
            return;
        }

        removeXp(p, amount);
        addStoredXp(p.getUniqueId(), amount);
        p.sendMessage(ChatColor.GREEN + "+ " + amount + " XP eingezahlt.");
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
    }

    private void depositAllXp(Player p) {
        int total = getRealTotalXP(p);
        if (total <= 0) return;

        removeXp(p, total);
        addStoredXp(p.getUniqueId(), total);
        p.sendMessage(ChatColor.GREEN + "+ " + total + " XP eingezahlt.");
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
    }

    private void withdrawXp(Player p, int amount) {
        int stored = getStoredXp(p.getUniqueId());
        if (stored < amount) {
            p.sendMessage(ChatColor.RED + "Nicht genug XP auf der Bank!");
            return;
        }
        addStoredXp(p.getUniqueId(), -amount);
        p.giveExp(amount);
        p.sendMessage(ChatColor.GREEN + "- " + amount + " XP ausgezahlt.");
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
    }

    private void withdrawAllXp(Player p) {
        int stored = getStoredXp(p.getUniqueId());
        if (stored <= 0) return;
        addStoredXp(p.getUniqueId(), -stored);
        p.giveExp(stored);
        p.sendMessage(ChatColor.GREEN + "- " + stored + " XP ausgezahlt.");
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
    }

    // ------------------------------------------------------------
    //  XP BOTTLES
    // ------------------------------------------------------------
    private void createXpBottle(Player p, int amount) {
        int stored = getStoredXp(p.getUniqueId());
        if (stored < amount) {
            p.sendMessage(ChatColor.RED + "Nicht genug XP auf der Bank!");
            return;
        }

        addStoredXp(p.getUniqueId(), -amount);

        ItemStack bottle = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta meta = bottle.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "XP Flasche (" + amount + ")");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + "Enthält: " + amount + " XP");
        meta.setLore(lore);
        bottle.setItemMeta(meta);

        p.getInventory().addItem(bottle);
        p.sendMessage(ChatColor.GREEN + "Flasche erstellt.");
    }

    // ------------------------------------------------------------
    //  STORAGE
    // ------------------------------------------------------------
    public void save() {
        plugin.saveConfig();
    }

    private int getStoredXp(UUID uuid) {
        return plugin.getConfig().getInt("players." + uuid.toString(), 0);
    }

    private void addStoredXp(UUID uuid, int amount) {
        int current = getStoredXp(uuid);
        plugin.getConfig().set("players." + uuid.toString(), current + amount);
    }

    private ItemStack createButton(Material mat, String name, String lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null) {
            List<String> l = new ArrayList<>();
            l.add(ChatColor.GRAY + lore);
            meta.setLore(l);
        }
        item.setItemMeta(meta);
        return item;
    }
}
