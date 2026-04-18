package de.bierxp;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class NPCManager {

    public Player spawnNPC(Location loc, String name, String skinValue, String skinSignature) {

        // 1. Fake-Player Profil erstellen
        PlayerProfile profile = Bukkit.createProfile(null, name);
        profile.getProperties().add(new ProfileProperty("textures", skinValue, skinSignature));

        // 2. NPC spawnen
        Player npc = (Player) loc.getWorld().spawnEntity(loc, EntityType.PLAYER);

        // 3. Skin setzen
        npc.setPlayerProfile(profile);

        // 4. NPC-Einstellungen
        npc.setAI(false);
        npc.setInvulnerable(true);
        npc.setCollidable(false);
        npc.setSilent(true);
        npc.setGravity(false);

        return npc;
    }
}
