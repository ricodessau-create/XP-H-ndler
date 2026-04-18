package de.bierxp;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NPCManager {

    private ServerPlayer npc;

    public void spawnNPC(Location loc, String name, String value, String signature) {

        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        ServerLevel nmsWorld = ((CraftPlayer) Bukkit.getOnlinePlayers().iterator().next()).getHandle().serverLevel();

        GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        profile.getProperties().put("textures", new Property("textures", value, signature));

        npc = new ServerPlayer(nmsServer, nmsWorld, profile);

        npc.setPos(loc.getX(), loc.getY(), loc.getZ());

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showEntity(Bukkit.getPluginManager().getPlugin("BierXP"), npc.getBukkitEntity());
        }
    }

    public void despawnNPC() {
        if (npc != null) {
            npc.getBukkitEntity().remove();
        }
    }
}
