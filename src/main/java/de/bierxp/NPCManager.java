package de.bierxp;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;

import java.util.UUID;

public class NPCManager {

    public ServerPlayer spawnNPC(Location loc, String name, String skinValue, String skinSignature) {

        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel nmsWorld = ((CraftWorld) loc.getWorld()).getHandle();

        GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        profile.getProperties().put("textures", new Property("textures", skinValue, skinSignature));

        ServerPlayer npc = new ServerPlayer(nmsServer, nmsWorld, profile);

        npc.setPos(loc.getX(), loc.getY(), loc.getZ());
        npc.setGameMode(GameType.ADVENTURE);

        nmsWorld.addFreshEntity(npc);

        return npc;
    }
}
