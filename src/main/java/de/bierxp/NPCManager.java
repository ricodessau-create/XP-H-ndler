package de.bierxp;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.ProfilePublicKey;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NPCManager {

    public ServerPlayer spawnNPC(Location loc, String name, String skinValue, String skinSignature) {

        // NMS Server & Welt holen
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel nmsWorld = ((CraftWorld) loc.getWorld()).getHandle();

        // Fake GameProfile mit Skin
        GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        profile.getProperties().put("textures", new Property("textures", skinValue, skinSignature));

        // Fake-Player erzeugen
        ServerPlayer npc = new ServerPlayer(nmsServer, nmsWorld, profile, ProfilePublicKey.createFake());

        // Position setzen
        npc.setPos(loc.getX(), loc.getY(), loc.getZ());
        npc.setYRot(loc.getYaw());
        npc.setXRot(loc.getPitch());

        // NPC für alle Spieler sichtbar machen
        for (Player p : Bukkit.getOnlinePlayers()) {
            var conn = ((CraftPlayer) p).getHandle().connection;

            // Spielerinfo senden (Name, Skin)
            conn.send(new net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket(
                    net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, npc));

            // NPC selbst spawnen
            conn.send(new net.minecraft.network.protocol.game.ClientboundAddPlayerPacket(npc));
        }

        return npc;
    }
}
