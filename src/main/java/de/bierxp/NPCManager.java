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
        [span_39](start_span)PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), name);[span_39](end_span)
        [span_40](start_span)// Skin-Daten setzen[span_40](end_span)
        if (!value.equals("DEIN_VALUE")) {
            [span_41](start_span)profile.setProperty(new ProfileProperty("textures", value, signature));[span_41](end_span)
        }

        npc = loc.getWorld().spawn(loc, PlayerDisplay.class, display -> {
            [span_42](start_span)display.setProfile(profile);[span_42](end_span)
            [span_43](start_span)display.setCustomName(Component.text("§6" + name));[span_43](end_span)
            [span_44](start_span)display.setCustomNameVisible(true);[span_44](end_span)
        });
    }

    public void despawnNPC() {
        if (npc != null) {
            [span_45](start_span)npc.remove();[span_45](end_span)
            [span_46](start_span)npc = null;[span_46](end_span)
        }
    }

    public boolean isNPC(org.bukkit.entity.Entity e) {
        [span_47](start_span)return npc != null && npc.getUniqueId().equals(e.getUniqueId());[span_47](end_span)
    }
        }
