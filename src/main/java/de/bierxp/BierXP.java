package de.bierxp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
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
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BierXP extends JavaPlugin implements Listener, CommandExecutor {

    private File dataFile;
    private FileConfiguration dataConfig;
    private List<UUID> bankers = new ArrayList<>();
    private Map<Player, Integer> bottleCreationQueue = new HashMap<>();

    private String bankerName = ChatColor.GOLD + "BierXP Bankier";
    private String guiTitle = ChatColor.DARK_GREEN + "BierXP Bank";

    @Override
    public void onEnable() {
        setupDataFile();
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("bierxp").setExecutor(this);
        loadBankers();
        getLogger().info("BierXP geladen!");
    }

    @Override
    public void onDisable() {
        saveData();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("bierxp.admin")) {
            sender.sendMessage(ChatColor.RED + "Keine Rechte.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("spawn")) {
            if (!(sender instanceof Player)) return true;
            Player p = (Player) sender;
            spawnBanker(p.getLocation());
            p.sendMessage(ChatColor.GREEN + "BierXP Bankier gespawnt.");
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Nutze: /bierxp spawn");
        return true;
    }

    // --- NPC Logik ---
    private void spawnBanker(Location loc) {
        Villager v = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
        v.setCustomName(bankerName);
        v.setCustomNameVisible(true);
        v.setProfession(Villager.Profession.LIBRARIAN);
        v.setVillagerType(Villager.Type.PLAINS);
        v.setAI(false);
        v.setInvulnerable(true);
        v.setSilent(true);
        v.setCollidable(false);
        
        bankers.add(v.getUniqueId());
        saveData();
    }
    
    private void loadBankers() {
        bankers.clear();
        if (dataConfig.contains("bankers")) {
            List<String> uuidStrings = dataConfig.getStringList("bankers");
            for (String uuidStr : uuidStrings) {
                try {
                    bankers.add(UUID.fromString(uuidStr));
                } catch (Exception e) {
                    getLogger().warning("Konnte Bankier UUID nicht laden: " + uuidStr);
                }
            }
        }
    }

    // --- Event: Bankier anklicken ---
    // Priorität LOWEST: Wir wollen ALS ERSTES reagieren, bevor antiTrade es cancelt
    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteractNPC(PlayerInteractEntityEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (!(e.getRightClicked() instanceof Villager)) return;

        Villager v = (Villager) e.getRightClicked();
        
        if (bankers.contains(v.getUniqueId())) {
            e.setCancelled(true); // Wir canceln es SOFORT für alle anderen (antiTrade, Vanilla Trading)
            openBankGUI(e.getPlayer());
        }
    }

    // --- GUI Logik ---
    private void openBankGUI(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, guiTitle);
        int storedXp = getStoredXp(p.getUniqueId());

        // Info Item
        ItemStack info = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName(ChatColor.GREEN + "Dein Guthaben");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.YELLOW + "Gespeichert: " + ChatColor.WHITE + storedXp + " XP");
        infoMeta.setLore(lore);
        info.setItemMeta(infoMeta);
        inv.setItem(13, info);

        // Einzahlen Buttons
        inv.setItem(10, createButton(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "100 XP einzahlen", null));
        inv.setItem(11, createButton(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "1000 XP einzahlen", null));
        inv.setItem(12, createButton(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "Alles einzahlen", null));

        // Auszahlen Buttons
        inv.setItem(14, createButton(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "100 XP auszahlen", null));
        inv.setItem(15, createButton(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "1000 XP auszahlen", null));
        inv.setItem(16, createButton(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Alles auszahlen", null));

        // Flasche erstellen
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
        
        // Einzahlen
        if (slot == 10) { depositXp(p, 100); openBankGUI(p); }
        if (slot == 11) { depositXp(p, 1000); openBankGUI(p); }
        if (slot == 12) { depositAllXp(p); openBankGUI(p); }
        
        // Auszahlen
        if (slot == 14) { withdrawXp(p, 100); openBankGUI(p); }
        if (slot == 15) { withdrawXp(p, 1000); openBankGUI(p); }
        if (slot == 16) { withdrawAllXp(p); openBankGUI(p); }
        
        // Flasche
        if (slot == 22) {
            p.closeInventory();
            p.sendMessage(ChatColor.AQUA + "Schreibe die Menge an XP in den Chat, die in die Flasche soll. (Abbrechen mit 'stop')");
            bottleCreationQueue.put(p, 1);
        }
    }

    // --- Chat Input für Flaschen ---
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
                getServer().getScheduler().runTask(this, () -> createXpBottle(p, finalAmount));
                
            } catch (NumberFormatException ex) {
                p.sendMessage(ChatColor.RED + "Ungültige Zahl. Versuche es erneut.");
            }
            
            bottleCreationQueue.remove(p);
        }
    }
    
    // --- XP Flasche werfen ---
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
        } catch (Exception ex) {
            // Parsing error
        }
    }

    // --- Mathematik ---
    
    private void depositXp(Player p, int amount) {
        if (p.getTotalExperience() < amount) {
            p.sendMessage(ChatColor.RED + "Nicht genug XP! (Du hast: " + p.getTotalExperience() + ")");
            return;
        }
        p.giveExp(-amount); 
        addStoredXp(p.getUniqueId(), amount);
        p.sendMessage(ChatColor.GREEN + "+ " + amount + " XP eingezahlt.");
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
    }

    private void depositAllXp(Player p) {
        int total = p.getTotalExperience();
        if (total <= 0) {
            p.sendMessage(ChatColor.RED + "Du hast keine XP.");
            return;
        }
        p.setTotalExperience(0);
        p.setLevel(0);
        p.setExp(0);
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
        if (stored <= 0) {
            p.sendMessage(ChatColor.RED + "Kein Guthaben vorhanden.");
            return;
        }
        addStoredXp(p.getUniqueId(), -stored);
        p.giveExp(stored);
        p.sendMessage(ChatColor.GREEN + "- " + stored + " XP ausgezahlt.");
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
    }
    
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
        lore.add(ChatColor.GRAY + "Rechtsklick zum Benutzen");
        meta.setLore(lore);
        bottle.setItemMeta(meta);
        
        p.getInventory().addItem(bottle);
        p.sendMessage(ChatColor.GREEN + "Flasche mit " + amount + " XP erstellt.");
    }

    // --- Data Storage ---
    private void setupDataFile() {
        dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            try { dataFile.createNewFile(); } catch (IOException e) {}
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    private void saveData() {
        try {
            // FIX: Konvertiere UUIDs zu Strings vor dem Speichern!
            List<String> uuidStrings = new ArrayList<>();
            for (UUID id : bankers) {
                uuidStrings.add(id.toString());
            }
            dataConfig.set("bankers", uuidStrings);
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getStoredXp(UUID uuid) {
        return dataConfig.getInt("players." + uuid.toString(), 0);
    }

    private void addStoredXp(UUID uuid, int amount) {
        int current = getStoredXp(uuid);
        dataConfig.set("players." + uuid.toString(), current + amount);
        saveData();
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
