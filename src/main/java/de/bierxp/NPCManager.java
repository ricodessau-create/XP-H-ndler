package de.bierxp;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Random;
import java.util.UUID;

public class NPCManager {

    private final ProtocolManager protocol = ProtocolLibrary.getProtocolManager();

    private UUID npcUUID;
    private int npcEntityId;

    public void spawnNPC(Location loc, String name, String value, String signature) {

        npcUUID = UUID.randomUUID();
        npcEntityId = (int) (Math.random() * Integer.MAX_VALUE);

        WrappedGameProfile profile = new WrappedGameProfile(npcUUID, name);
        profile.getProperties().put("textures", new WrappedSignedProperty("textures", value, signature));

        // 1) PlayerInfo ADD_PLAYER
        PacketContainer info = protocol.createPacket(PacketType.Play.Server.PLAYER_INFO);
        info.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);

        PlayerInfoData data = new PlayerInfoData(
                profile,
                0,
                EnumWrappers.NativeGameMode.SURVIVAL,
                WrappedChatComponent.fromText(name)
        );

        info.getPlayerInfoDataLists().write(0, Collections.singletonList(data));

        // 2) Spawn NPC
        PacketContainer spawn = protocol.createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
        spawn.getIntegers().write(0, npcEntityId);
        spawn.getUUIDs().write(0, npcUUID);
        spawn.getDoubles().write(0, loc.getX());
        spawn.getDoubles().write(1, loc.getY());
        spawn.getDoubles().write(2, loc.getZ());
        spawn.getBytes().write(0, (byte) (loc.getYaw() * 256 / 360));
        spawn.getBytes().write(1, (byte) (loc.getPitch() * 256 / 360));

        // 3) Metadata (NameTag sichtbar)
        PacketContainer meta = protocol.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        meta.getIntegers().write(0, npcEntityId);
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        watcher.setObject(0, WrappedDataWatcher.Registry.get(Byte.class), (byte) 0);
        meta.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());

        // Senden
        for (Player p : Bukkit.getOnlinePlayers()) {
            try {
                protocol.sendServerPacket(p, info);
                protocol.sendServerPacket(p, spawn);
                protocol.sendServerPacket(p, meta);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        startAnimation();
        startInteractionListener();
    }

    // Animation: NPC dreht den Kopf leicht
    private void startAnimation() {
        Bukkit.getScheduler().runTaskTimer(
                Bukkit.getPluginManager().getPlugin("BierXP"),
                () -> {
                    if (npcEntityId == 0) return;

                    PacketContainer look = protocol.createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
                    look.getIntegers().write(0, npcEntityId);
                    byte yaw = (byte) (new Random().nextInt(256));
                    look.getBytes().write(0, yaw);

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        try {
                            protocol.sendServerPacket(p, look);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                20, 20
        );
    }

    // Interaktion: Rechtsklick erkennen
    private void startInteractionListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new NPCListener(npcEntityId));
    }

    // Despawn
    public void despawnNPC() {
        if (npcEntityId == 0) return;

        PacketContainer destroy = protocol.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        destroy.getIntLists().write(0, Collections.singletonList(npcEntityId));

        for (Player p : Bukkit.getOnlinePlayers()) {
            try {
                protocol.sendServerPacket(p, destroy);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        npcEntityId = 0;
        npcUUID = null;
    }
}
