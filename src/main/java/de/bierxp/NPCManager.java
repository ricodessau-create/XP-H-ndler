package de.bierxp;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.PlayerDisplay;
import java.util.UUID;

public class NPCManager {

    private PlayerDisplay npc;

    public void spawnNPC(Location loc, String name, String value, String signature) {
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), name);
        
        // Skin-Daten setzen (Nur wenn gültige Daten vorhanden sind)
        if (value != null && !value.isEmpty() && !value.equals("DEIN_VALUE")) {
            profile.setProperty(new ProfileProperty("textures", value, signature));
        }

        npc = loc.getWorld().spawn(loc, PlayerDisplay.class, display -> {
            display.setProfile(profile);
            display.setCustomName(Component.text("§6" + name));
            display.setCustomNameVisible(true);
        });
    }

    public void despawnNPC() {
        if (npc != null) {
            npc.remove();
            npc = null;
        }
    }

    public boolean isNPC(org.bukkit.entity.Entity e) {
        return npc != null && npc.getUniqueId().equals(e.getUniqueId());
    }
}
