package de.bierxp;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.UUID;

public class NPCManager {

    private final ProtocolManager pm = ProtocolLibrary.getProtocolManager();
    private int entityId = 123456; // egal, nur eindeutig
    private UUID uuid = UUID.randomUUID();

    public void spawnNPC(Location loc, String name, String value, String signature) {

        WrappedGameProfile profile = new WrappedGameProfile(uuid, name);

        // Skin direkt setzen – ohne GET_PROPERTIES
        profile.getProperties().put("textures", new WrappedSignedProperty("textures", value, signature));

        // PlayerInfo Packet
        PacketContainer info = pm.createPacket(com.comphenix.protocol.PacketType.Play.Server.PLAYER_INFO);
        info.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        info.getPlayerInfoDataLists().write(0, Collections.singletonList(
                new PlayerInfoData(profile, 0, EnumWrappers.NativeGameMode.SURVIVAL, null)
        ));

        // Spawn Packet
        PacketContainer spawn = pm.createPacket(com.comphenix.protocol.PacketType.Play.Server.NAMED_ENTITY_SPAWN);
        spawn.getIntegers().write(0, entityId);
        spawn.getUUIDs().write(0, uuid);
        spawn.getDoubles().write(0, loc.getX());
        spawn.getDoubles().write(1, loc.getY());
        spawn.getDoubles().write(2, loc.getZ());
        spawn.getBytes().write(0, (byte) 0);
        spawn.getBytes().write(1, (byte) 0);

        for (Player p : Bukkit.getOnlinePlayers()) {
            pm.sendServerPacket(p, info);
            pm.sendServerPacket(p, spawn);
        }
    }

    public void despawnNPC() {
        PacketContainer destroy = pm.createPacket(com.comphenix.protocol.PacketType.Play.Server.ENTITY_DESTROY);
        destroy.getIntLists().write(0, Collections.singletonList(entityId));

        for (Player p : Bukkit.getOnlinePlayers()) {
            pm.sendServerPacket(p, destroy);
        }
    }
}
