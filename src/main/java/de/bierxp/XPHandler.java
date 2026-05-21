package de.bierxp;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.UUID;

public class XPHandler implements Listener {

    private final BierXP plugin;
    private static final String GUI_TITLE = "§6Rico's XP-Bank";

    public XPHandler(BierXP plugin) {
        this.plugin = plugin;
    }

    public void openBankGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, GUI_TITLE);
        int balance = getBalance(player.getUniqueId());
        int playerXP = getTotalExperience(player);

        inv.setItem(1, createItem(Material.ENCHANTED_BOOK, "§aAlle XP einzahlen", "§7Zahlt dein gesamtes XP ein"));
        inv.setItem(2, createItem(Material.ENCHANTED_BOOK, "§a64 XP einzahlen",   "§7Wenn du ≥ 64 XP hast"));
        inv.setItem(3, createItem(Material.ENCHANTED_BOOK, "§a10 XP einzahlen",   "§7Wenn du ≥ 10 XP hast"));
        inv.setItem(4, createItem(Material.ENCHANTED_BOOK, "§a1 XP einzahlen",    "§7Wenn du ≥ 1 XP hast"));

        inv.setItem(13, createItem(Material.NETHER_STAR,
                "§eKontostand",
                "§f" + balance + " XP gespeichert",
                "§7Du trägst: §f" + playerXP + " XP"));

        inv.setItem(19, createItem(Material.GOLD_NUGGET,       "§6Alle XP auszahlen", "§7Direkt als XP"));
        inv.setItem(20, createItem(Material.GOLD_NUGGET,       "§664 XP auszahlen",   "§7Direkt als XP"));
        inv.setItem(21, createItem(Material.GOLD_NUGGET,       "§610 XP auszahlen",   "§7Direkt als XP"));
        inv.setItem(22, createItem(Material.GOLD_NUGGET,       "§61 XP auszahlen",    "§7Direkt als XP"));
        inv.setItem(23, createItem(Material.EXPERIENCE_BOTTLE, "§b64 XP als Flasche", "§7Als XP-Flasche ins Inventar"));
        inv.setItem(24, createItem(Material.EXPERIENCE_BOTTLE, "§b10 XP als Flasche", "§7Als XP-Flasche ins Inventar"));
        inv.setItem(25, createItem(Material.EXPERIENCE_BOTTLE, "§b1 XP als Flasche",  "§7Als XP-Flasche ins Inventar"));

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        String title = PlainTextComponentSerializer.plainText().serialize(e.getView().title());
        if (!stripColor(title).equals(stripColor(GUI_TITLE))) return;

        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player player)) return;
        ItemStack item = e.getCurrentItem();
        if (item == null || !item.hasItemMeta()) return;

        String name = item.getItemMeta().getDisplayName();
        UUID uuid = player.getUniqueId();

        if (item.getType() == Material.ENCHANTED_BOOK) {
            int playerXP = getTotalExperience(player);
            int amount;
            if (name.contains("Alle"))    amount = playerXP;
            else if (name.contains("64")) amount = 64;
            else if (name.contains("10")) amount = 10;
            else                          amount = 1;

            if (playerXP < 1) { player.sendMessage("§cDu hast gar keine XP!"); return; }
            if (playerXP < amount) { player.sendMessage("§cDu hast nur §f" + playerXP + " §cXP!"); return; }

            setTotalExperience(player, playerXP - amount);
            setBalance(uuid, getBalance(uuid) + amount);
            player.sendMessage("§a" + amount + " XP eingezahlt.");
            openBankGUI(player);

        } else if (item.getType() == Material.GOLD_NUGGET) {
            int balance = getBalance(uuid);
            int amount;
            if (name.contains("Alle"))    amount = balance;
            else if (name.contains("64")) amount = 64;
            else if (name.contains("10")) amount = 10;
            else                          amount = 1;

            if (balance < 1) { player.sendMessage("§cDeine Bank ist leer!"); return; }
            if (balance < amount) { player.sendMessage("§cDu hast nur §f" + balance + " §cXP in der Bank!"); return; }

            setBalance(uuid, balance - amount);
            setTotalExperience(player, getTotalExperience(player) + amount);
            player.sendMessage("§6" + amount + " XP ausgezahlt.");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            openBankGUI(player);

        } else if (item.getType() == Material.EXPERIENCE_BOTTLE) {
            int balance = getBalance(uuid);
            int amount;
            if (name.contains("64"))      amount = 64;
            else if (name.contains("10")) amount = 10;
            else                          amount = 1;

            if (balance < amount) { player.sendMessage("§cNicht genug XP! (§f" + balance + " §cvorhanden)"); return; }
            if (player.getInventory().firstEmpty() == -1) { player.sendMessage("§cDein Inventar ist voll!"); return; }

            setBalance(uuid, balance - amount);
            player.getInventory().addItem(createXPBottle(amount));
            player.sendMessage("§b" + amount + " XP-Flasche(n) erhalten.");
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            openBankGUI(player);
        }
    }

    private int getBalance(UUID uuid) {
        return plugin.getConfig().getInt("players." + uuid, 0);
    }

    private void setBalance(UUID uuid, int amount) {
        plugin.getConfig().set("players." + uuid, amount);
        plugin.saveConfig();
    }

    public int getTotalExperience(Player player) {
        int level = player.getLevel();
        float progress = player.getExp();
        int total = 0;
        for (int i = 0; i < level; i++) total += getExpToNextLevel(i);
        total += Math.round(progress * getExpToNextLevel(level));
        return total;
    }

    private void setTotalExperience(Player player, int exp) {
        if (exp < 0) exp = 0;
        player.setExp(0f);
        player.setLevel(0);
        player.setTotalExperience(0);
        player.giveExp(exp);
    }

    private int getExpToNextLevel(int level) {
        if (level <= 15) return 2 * level + 7;
        if (level <= 30) return 5 * level - 38;
        return 9 * level - 158;
    }

    private ItemStack createItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore.length > 0) meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createXPBottle(int xpAmount) {
        ItemStack bottle = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta meta = bottle.getItemMeta();
        meta.setDisplayName("§bXP-Flasche §f(" + xpAmount + " XP)");
        meta.setLore(Arrays.asList("§7Wert: §f" + xpAmount + " XP", "§7Rechtsklick zum Einlösen beim Dealer"));
        bottle.setItemMeta(meta);
        return bottle;
    }

    private String stripColor(String s) {
        return s.replaceAll("§[0-9a-fk-or]", "");
    }
}
