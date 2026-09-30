package de.bierxp;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
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

    public void loadNPCDelayed() {
        Bukkit.getScheduler().runTaskLater(
                plugin,
                () -> tryLoadNPC(0),
                20L
        );
    }

    private void tryLoadNPC(int attempt) {
        if (!plugin.isEnabled()) {
            return;
        }

        if (!CitizensAPI.hasImplementation()) {
            if (attempt < 10) {
                Bukkit.getScheduler().runTaskLater(
                        plugin,
                        () -> tryLoadNPC(attempt + 1),
                        20L
                );
            } else {
                plugin.getLogger().severe(
                        "Citizens ist nach mehreren Ladeversuchen nicht bereit."
                );
            }

            return;
        }

        NPCRegistry registry = CitizensAPI.getNPCRegistry();

        if (registry == null) {
            if (attempt < 10) {
                Bukkit.getScheduler().runTaskLater(
                        plugin,
                        () -> tryLoadNPC(attempt + 1),
                        20L
                );
            } else {
                plugin.getLogger().severe(
                        "Die Citizens-NPC-Registry konnte nicht geladen werden."
                );
            }

            return;
        }

        loadNPC();
    }

    public void loadNPC() {
        if (!CitizensAPI.hasImplementation()) {
            plugin.getLogger().warning(
                    "Citizens ist noch nicht bereit."
            );
            return;
        }

        NPCRegistry registry = CitizensAPI.getNPCRegistry();

        if (registry == null) {
            plugin.getLogger().warning(
                    "Die Citizens-NPC-Registry ist nicht verfügbar."
            );
            return;
        }

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
            plugin.getLogger().warning(
                    "Der gespeicherte XP-Dealer mit der ID "
                            + npcId
                            + " wurde bei Citizens nicht gefunden."
            );
            npc = null;
            return;
        }

        npc = storedNPC;

        configureNPC(npc);

        Location location = loadLocation();

        if (location == null) {
            plugin.getLogger().warning(
                    "Die gespeicherte Position des XP-Dealers konnte nicht geladen werden."
            );
            return;
        }

        if (npc.isSpawned()) {
            Location currentLocation = npc.getEntity().getLocation();

            if (!sameLocation(currentLocation, location)) {
                npc.despawn();
                npc.spawn(location);
            }
        } else {
            if (!npc.spawn(location)) {
                plugin.getLogger().warning(
                        "Der gespeicherte XP-Dealer konnte nicht gespawnt werden."
                );
                return;
            }
        }

        plugin.getLogger().info(
                "Rico der XP-Dealer wurde nach dem Serverstart geladen."
        );
    }

    public boolean spawnNPC(Location location) {
        if (location == null || location.getWorld() == null) {
            return false;
        }

        if (!CitizensAPI.hasImplementation()) {
            return false;
        }

        NPCRegistry registry = CitizensAPI.getNPCRegistry();

        if (registry == null) {
            return false;
        }

        removeExistingNPC();

        npc = registry.createNPC(
                EntityType.PLAYER,
                NPC_NAME
        );

        configureNPC(npc);

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
            int storedId = plugin.getConfig().getInt(
                    "npc.id",
                    -1
            );

            if (storedId <= 0) {
                return false;
            }

            if (!CitizensAPI.hasImplementation()) {
                return false;
            }

            NPCRegistry registry = CitizensAPI.getNPCRegistry();

            if (registry == null) {
                return false;
            }

            npc = registry.getById(storedId);

            if (npc == null) {
                return false;
            }
        }

        if (!CitizensAPI.hasImplementation()) {
            return false;
        }

        NPCRegistry registry = CitizensAPI.getNPCRegistry();

        if (registry == null) {
            return false;
        }

        if (npc.isSpawned()) {
            npc.despawn();
        }

        registry.deregister(npc);

        npc = null;

        plugin.getConfig().set(
                "npc.id",
                -1
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

        if (!CitizensAPI.hasImplementation()) {
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

        NPCRegistry registry = CitizensAPI.getNPCRegistry();

        if (registry != null) {
            registry.saveToStore();
        }
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

    private void configureNPC(NPC npc) {
        npc.setName(NPC_NAME);
        npc.setProtected(true);
        npc.setUseMinecraftAI(false);
        npc.setFlyable(false);
    }

    private void removeExistingNPC() {
        if (npc == null) {
            return;
        }

        if (!CitizensAPI.hasImplementation()) {
            npc = null;
            return;
        }

        NPCRegistry registry = CitizensAPI.getNPCRegistry();

        if (registry == null) {
            npc = null;
            return;
        }

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
                    "Die Welt '"
                            + worldName
                            + "' des XP-Dealers existiert nicht."
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

    private boolean sameLocation(
            Location first,
            Location second
    ) {
        if (first == null
                || second == null
                || first.getWorld() == null
                || second.getWorld() == null) {
            return false;
        }

        if (!first.getWorld().getName().equals(
                second.getWorld().getName()
        )) {
            return false;
        }

        return Math.abs(first.getX() - second.getX()) < 0.1
                && Math.abs(first.getY() - second.getY()) < 0.1
                && Math.abs(first.getZ() - second.getZ()) < 0.1;
    }
}
