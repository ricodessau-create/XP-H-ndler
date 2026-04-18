package de.bierxp;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;

import java.util.UUID;

public class NPCManager {

    private HumanEntity npc;

    public void spawnNPC(Location loc, String name, String value, String signature) {

        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), name);
        profile.setProperty(new ProfileProperty("textures", value, signature));

        npc = (HumanEntity) loc.getWorld().spawnEntity(loc, EntityType.PLAYER);
        npc.setPlayerProfile(profile);
        npc.setCustomName(name);
        npc.setCustomNameVisible(true);
        npc.setAI(false);
        npc.setInvulnerable(true);
    }

    public void despawnNPC() {
        if (npc != null) {
            npc.remove();
        }
    }
}
