package de.bierxp;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

public class NPCManager {

    public static final String NPC_NAME = "Rico der XP-Dealer";

    private final BierXP plugin;
    private NPC npc;

    public NPCManager(BierXP plugin) {
        this.plugin = plugin;
    }

    public void loadNPC() {
        NPCRegistry registry = CitizensAPI.getNPCRegistry();

        int npcId = plugin.getConfig().getInt(
                "npc.id",
                -1
        );

        if (npcId <= 0) {
            npc = null;
            return;
        }

        NPC storedNPC = registry.getById(npcId);

        if (storedNPC == null) {
            npc = null;
            return;
        }

        npc = storedNPC;
        npc.setName(NPC_NAME);
        npc.setProtected(true);
        npc.setUseMinecraftAI(false);
        npc.setFlyable(false);

        Location location = loadLocation();

        if (location == null) {
            return;
        }

        if (!npc.isSpawned()) {
            if (!npc.spawn(location)) {
                plugin.getLogger().warning(
                        "Der gespeicherte XP-Dealer konnte nicht gespawnt werden."
                );
            }
        }
    }

    public boolean spawnNPC(Location location) {
        if (location == null || location.getWorld() == null) {
            return false;
        }

        NPCRegistry registry = CitizensAPI.getNPCRegistry();

        removeExistingNPC();

        npc = registry.createNPC(
                EntityType.PLAYER,
                NPC_NAME
        );

        npc.setName(NPC_NAME);
        npc.setProtected(true);
        npc.setUseMinecraftAI(false);
        npc.setFlyable(false);

        boolean spawned = npc.spawn(location);

        if (!spawned) {
            registry.deregister(npc);
            npc = null;
            return false;
        }

        saveLocation(location);

        plugin.getConfig().set(
                "npc.id",
                npc.getId()
        );

        plugin.saveConfig();

        registry.saveToStore();

        return true;
    }

    public boolean despawnNPC() {
        if (npc == null) {
            return false;
        }

        NPCRegistry registry = CitizensAPI.getNPCRegistry();

        if (npc.isSpawned()) {
            npc.despawn();
        }

        registry.deregister(npc);

        npc = null;

        plugin.getConfig().set(
                "npc.id",
                null
        );

        plugin.getConfig().set(
                "npc.location",
                null
        );

        plugin.saveConfig();

        registry.saveToStore();

        return true;
    }

    public void saveNPCState() {
        if (npc == null) {
            return;
        }

        if (npc.isSpawned() && npc.getEntity() != null) {
            saveLocation(
                    npc.getEntity().getLocation()
            );
        }

        plugin.getConfig().set(
                "npc.id",
                npc.getId()
        );

        plugin.saveConfig();

        CitizensAPI.getNPCRegistry().saveToStore();
    }

    public boolean isNPC(NPC clickedNPC) {
        if (clickedNPC == null || npc == null) {
            return false;
        }

        return clickedNPC.getId() == npc.getId();
    }

    public NPC getNPC() {
        return npc;
    }

    private void removeExistingNPC() {
        if (npc == null) {
            return;
        }

        NPCRegistry registry = CitizensAPI.getNPCRegistry();

        if (npc.isSpawned()) {
            npc.despawn();
        }

        registry.deregister(npc);

        npc = null;
    }

    private void saveLocation(Location location) {
        if (location == null || location.getWorld() == null) {
            return;
        }

        plugin.getConfig().set(
                "npc.location.world",
                location.getWorld().getName()
        );

        plugin.getConfig().set(
                "npc.location.x",
                location.getX()
        );

        plugin.getConfig().set(
                "npc.location.y",
                location.getY()
        );

        plugin.getConfig().set(
                "npc.location.z",
                location.getZ()
        );

        plugin.getConfig().set(
                "npc.location.yaw",
                location.getYaw()
        );

        plugin.getConfig().set(
                "npc.location.pitch",
                location.getPitch()
        );

        plugin.saveConfig();
    }

    private Location loadLocation() {
        String worldName = plugin.getConfig().getString(
                "npc.location.world"
        );

        if (worldName == null || worldName.isBlank()) {
            return null;
        }

        World world = plugin.getServer().getWorld(worldName);

        if (world == null) {
            plugin.getLogger().warning(
                    "Die Welt '" + worldName + "' des XP-Dealers existiert nicht."
            );
            return null;
        }

        double x = plugin.getConfig().getDouble(
                "npc.location.x"
        );

        double y = plugin.getConfig().getDouble(
                "npc.location.y"
        );

        double z = plugin.getConfig().getDouble(
                "npc.location.z"
        );

        float yaw = (float) plugin.getConfig().getDouble(
                "npc.location.yaw"
        );

        float pitch = (float) plugin.getConfig().getDouble(
                "npc.location.pitch"
        );

        return new Location(
                world,
                x,
                y,
                z,
                yaw,
                pitch
        );
    }
}
