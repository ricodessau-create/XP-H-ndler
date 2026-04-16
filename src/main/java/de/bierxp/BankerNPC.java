package de.bierxp;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

public class BankerNPC {

    private static final String SKIN_NAME = "Notch"; // Name, von dem der Skin geladen wird (oder eigenes Profil)

    public static void spawnNPC(Location loc, String name, UUID uuid) {
        try {
            // Paper Profil erstellen
            Object server = getMethod(Bukkit.getServer(), "getServer").invoke(Bukkit.getServer());
            Object worldServer = getMethod(server.getClass(), "getLevel").invoke(server); // Paper methode
            
            // GameProfile erstellen
            GameProfile profile = new GameProfile(uuid, name);
            setSkin(profile);

            // EntityPlayer erstellen via NMS
            Class<?> entityPlayerClass = getNmsClass("server.level.EntityPlayer");
            Class<?> minecraftServerClass = getNmsClass("server.MinecraftServer");
            Class<?> worldServerClass = getNmsClass("server.level.WorldServer");
            Class<?> playerInteractManagerClass = getNmsClass("server.level.PlayerInteractManager");
            
            Constructor<?> constr = entityPlayerClass.getConstructor(minecraftServerClass, worldServerClass, GameProfile.class);
            Object interactManager = playerInteractManagerClass.getConstructor(worldServerClass).newInstance(worldServer);
            
            Object entityPlayer = constr.newInstance(server, worldServer, profile);

            // Location setzen
            setPos(entityPlayer, loc);

            // Zur Welt hinzufügen
            Method addEntity = worldServer.getClass().getDeclaredMethod("addFreshEntity", getNmsClass("world.entity.Entity"));
            addEntity.invoke(worldServer, entityPlayer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setPos(Object entity, Location loc) throws Exception {
        Method setLocation = entity.getClass().getMethod("setPos", double.class, double.class, double.class);
        setLocation.invoke(entity, loc.getX(), loc.getY(), loc.getZ());
        // Rotation
        Method setYRot = entity.getClass().getMethod("setYRot", float.class);
        setYRot.invoke(entity, loc.getYaw());
        Method setXRot = entity.getClass().getMethod("setXRot", float.class);
        setXRot.invoke(entity, loc.getPitch());
    }

    private static void setSkin(GameProfile profile) {
        // Skin von einem Spieler laden (z.B. Notch oder eigener Skin-String)
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(SKIN_NAME);
        if (offlinePlayer.getPlayerProfile() != null && offlinePlayer.getPlayerProfile().getTextures().getSkin() != null) {
            profile.getProperties().put("textures", new Property("textures", 
                offlinePlayer.getPlayerProfile().getTextures().getSkin().toString(),
                offlinePlayer.getPlayerProfile().getTextures().getSignature() != null ? offlinePlayer.getPlayerProfile().getTextures().getSignature().toString() : null));
        }
    }

    private static Class<?> getNmsClass(String path) throws ClassNotFoundException {
        return Class.forName("net.minecraft." + path);
    }

    private static Method getMethod(Class<?> clazz, String name) {
        for (Method m : clazz.getMethods()) {
            if (m.getName().equals(name)) return m;
        }
        return null;
    }
}
