package de.bierxp;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class BierXP extends JavaPlugin implements CommandExecutor {

    private static BierXP instance;
    private NPCManager npcManager;
    private XPHandler xpHandler;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        npcManager = new NPCManager();
        xpHandler = new XPHandler(this);

        if (getCommand("bierxp") != null) {
            getCommand("bierxp").setExecutor(this);
        }

        getServer().getPluginManager().registerEvents(xpHandler, this);

        getLogger().info("BierXP geladen!");
    }

    @Override
    public void onDisable() {
        if (xpHandler != null) xpHandler.save();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl nutzen.");
            return true;
        }

        Player p = (Player) sender;

        if (!p.hasPermission("bierxp.admin")) {
            p.sendMessage("Keine Rechte.");
            return true;
        }

        // /bierxp spawn
        if (args.length == 1 && args[0].equalsIgnoreCase("spawn")) {

            String skinValue =
                    "ewogICJ0aW1lc3RhbXAiIDogMTc3NjM3OTE5NjExOCwKICAicHJvZmlsZUlkIiA6ICJmYzVhNDY3MDA0ZmM0MzM4OWE1MzgwNDBkY2QxM2Q2OCIsCiAgInByb2ZpbGVOYW1lIiA6ICJSaWNvRGVzc2F1IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ExZGFhMzlkNjY5MDY0ZGRjMDQ2NjgyNDE4MjAwMzM0MWNjNTA5NTdjODU1YjBlMTA0Y2EzYTY3MzhjODYxMzAiCiAgICB9CiAgfQp9";

            String skinSignature =
                    "n/OVwJbYfh7oivXp39CKKFE1jGkjgoUhUVTV6nTXPLL9gF5wcTEgiRZtGrG7BNCkOJCCAp3o4wj/jgsrDKj7ix5Qrq+CyWddCDro1N+nid9zggPFpZWZL5XIg7457RC95dAflHcwVJLMqC1xBegTUbp9xQqQrVbXty/pVov0xrqv3sSTSeod9O/ZXfWQLpg/btpo8movBUYC1p7tTO5yeRu+mCxq2vMOriqnkhmx2buv35amqyN2sIY3M6OnYUK2EIbgf1yF7t9AkZp/cvneAevR+9voO67qmkFeIeUIgIOWeX89tVb1TtZ3nCJGtckS40I3ha5gV7tGRJ0aa2CH6Tm55tTKRmcCk1/nRZZ4uwMnpM6NV3DrqBy2JuwGrGXwZ1ANFO0VlA70F2U2CNFFKgzWp2JObCw4E/Ufz/bA+VVG9ajN6VdFypto8vk1c3x4o9O2jKQ7JkbWv4SVpksUYe7MogOEiJgaPnUhJDQe973ARtHfBJtlja/yW2IoFOG+sV10d+5GcWGxls0YsZ8PlGKvGvaSkFdcSMx34BzFrRkijNBqOS5flBTD3dbYUj4GbXU5gj+dpniSnI8LTJDwAG5xXvqAA4mIvN6uP2zx+XYhQytIiEcpXGQU1Me5ytm6+cJ9UR/Z/83tQBqJfKoX1rrLKLO4Et0sgIDZ2EhWhMU=";

            Player npc = npcManager.spawnNPC(
                    p.getLocation(),
                    "BierXP Banker",
                    skinValue,
                    skinSignature
            );

            getConfig().set("banker-npc", npc.getUniqueId().toString());
            saveConfig();

            p.sendMessage("Bankier gespawnt.");
            return true;
        }

        // /bierxp despawn
        if (args.length == 1 && args[0].equalsIgnoreCase("despawn")) {

            String uuid = getConfig().getString("banker-npc");
            if (uuid == null) {
                p.sendMessage("Kein Banker gespeichert.");
                return true;
            }

            try {
                Entity e = Bukkit.getEntity(UUID.fromString(uuid));
                if (e != null) {
                    e.remove();
                    p.sendMessage("Bankier entfernt.");
                } else {
                    p.sendMessage("Bankier nicht gefunden.");
                }
            } catch (Exception ex) {
                p.sendMessage("Gespeicherte Banker-UUID ist ungültig.");
            }

            getConfig().set("banker-npc", null);
            saveConfig();
            return true;
        }

        p.sendMessage("Nutze: /bierxp spawn oder /bierxp despawn");
        return true;
    }

    public static BierXP getInstance() {
        return instance;
    }

    public NPCManager getNPCManager() {
        return npcManager;
    }

    public XPHandler getXPHandler() {
        return xpHandler;
    }
}
