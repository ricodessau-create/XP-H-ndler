package de.bierxp;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.FakePlayer;

import java.util.UUID;

public class NPCManager {

    public FakePlayer spawnNPC(Location loc, String name, String skinValue, String skinSignature) {

        // Profil erstellen
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), name);
        profile.getProperties().add(new ProfileProperty("textures", skinValue, skinSignature));

        // FakePlayer spawnen (Paper/Purpur API)
        FakePlayer npc = loc.getWorld().spawnFakePlayer(loc, profile);

        // Einstellungen
        npc.setAI(false);
        npc.setInvulnerable(true);
        npc.setCollidable(false);
        npc.setSilent(true);
        npc.setGravity(false);

        return npc;
    }
}
