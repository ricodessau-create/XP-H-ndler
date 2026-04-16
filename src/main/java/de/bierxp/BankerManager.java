package de.bierxp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BankerManager {

    private final BierXP plugin;
    private final File dataFile;
    private final FileConfiguration dataConfig;
    private final List<UUID> bankers = new ArrayList<>();
    
    private String bankerName = ChatColor.GOLD + "BierXP Bankier";

    public BankerManager(BierXP plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "bankers.yml");
        if (!dataFile.exists()) {
            try { dataFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
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
        if (dataConfig.contains("bankers")) {
            for (String uuidStr : dataConfig.getStringList("bankers")) {
                try {
                    UUID id = UUID.fromString(uuidStr);
                    bankers.add(id);
                } catch (Exception e) { }
            }
        }
    }

    public void save() {
        List<String> uuidStrings = new ArrayList<>();
        for (UUID id : bankers) uuidStrings.add(id.toString());
        dataConfig.set("bankers", uuidStrings);
        try { dataConfig.save(dataFile); } catch (IOException e) { e.printStackTrace(); }
    }
}
