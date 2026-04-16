package de.bierxp;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BankerManager {

    private final BierXP plugin;
    private final List<UUID> bankers = new ArrayList<>();
    
    private String bankerName = ChatColor.GOLD + "BierXP Bankier";

    public BankerManager(BierXP plugin) {
        this.plugin = plugin;
        load();
    }

    public void spawnBanker(Location loc) {
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
        save();
    }
    
    public boolean isBanker(Entity entity) {
        return bankers.contains(entity.getUniqueId());
    }

    private void load() {
        bankers.clear();
        // Lädt aus der Haupt-config (data.yml), wie im alten Plugin
        FileConfiguration config = plugin.getConfig();
        if (config.contains("bankers")) {
            for (String uuidStr : config.getStringList("bankers")) {
                try {
                    bankers.add(UUID.fromString(uuidStr));
                } catch (Exception e) { }
            }
        }
    }

    public void save() {
        // Speichert in die Haupt-config (data.yml)
        FileConfiguration config = plugin.getConfig();
        List<String> uuidStrings = new ArrayList<>();
        for (UUID id : bankers) uuidStrings.add(id.toString());
        config.set("bankers", uuidStrings);
        plugin.saveConfig();
    }
}
