package de.bierxp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BierXP extends JavaPlugin {

    private NPCManager npcManager;
    private XPHandler xpHandler;

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            getLogger().severe("ProtocolLib nicht gefunden! Plugin deaktiviert.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        npcManager = new NPCManager();
        xpHandler = new XPHandler(this);

        // Registriere Events
        Bukkit.getPluginManager().registerEvents(new NPCClickListener(npcManager), this);
        Bukkit.getPluginManager().registerEvents(xpHandler, this);

        // Hauptbefehl registrieren
        getCommand("bierxp").setExecutor(this);

        getLogger().info("BierXP gestartet (Dupe-Fix aktiv).");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (!cmd.getName().equalsIgnoreCase("bierxp")) return false;

        if (args.length == 0) {
            player.sendMessage("§eNutze: /bierxp <spawn|despawn>");
            return true;
        }

        if (args[0].equalsIgnoreCase("spawn")) {
            Location loc = player.getLocation().add(player.getLocation().getDirection().normalize().multiply(2));
            npcManager.spawnNPC(
                    loc,
                    "RicoDessau",
                    "ewogICJ0aW1lc3RhbXAiIDogMTc3NjM3OTE5NjExOCwKICAicHJvZmlsZUlkIiA6ICJmYzVhNDY3MDA0ZmM0MzM4OWE1MzgwNDBkY2QxM2Q2OCIsCiAgInByb2ZpbGVOYW1lIiA6ICJSaWNvRGVzc2F1IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ExZGFhMzlkNjY5MDY0ZGRjMDQ2NjgyNDE4MjAwMzM0MWNjNTA5NTdjODU1YjBlMTA0Y2EzYTY3MzhjODYxMzAiCiAgICB9CiAgfQp9",
                    "n/OVwJbYfh7oivXp39CKKFE1jGkjgoUhUVTV6nTXPLL9gF5wcTEgiRZtGrG7BNCkOJCCAp3o4wj/jgsrDKj7ix5Qrq+CyWddCDro1N+nid9zggPFpZWZL5XIg7457RC95dAflHcwVJLMqC1xBegTUbp9xQqQrVbXty/pVov0xrqv3sSTSeod9O/ZXfWQLpg/btpo8movBUYC1p7tTO5yeRu+mCxq2vMOriqnkhmx2buv35amqyN2sIY3M6OnYUK2EIbgf1yF7t9AkZp/cvneAevR+9voO67qmkFeIeUIgIOWeX89tVb1TtZ3nCJGtckS40I3ha5gV7tGRJ0aa2CH6Tm55tTKRmcCk1/nRZZ4uwMnpM6NV3DrqBy2JuwGrGXwZ1ANFO0VlA70F2U2CNFFKgzWp2JObCw4E/Ufz/bA+VVG9ajN6VdFypto8vk1c3x4o9O2jKQ7JkbWv4SVpksUYe7MogOEiJgaPnUhJDQe973ARtHfBJtlja/yW2IoFOG+sV10d+5GcWGxls0YsZ8PlGKvGvaSkFdcSMx34BzFrRkijNBqOS5flBTD3dbYUj4GbXU5gj+dpniSnI8LTJDwAG5xXvqAA4mIvN6uP2zx+XYhQytIiEcpXGQU1Me5ytm6+cJ9UR/Z/83tQBqJfKoX1rrLKLO4Et0sgIDZ2EhWhMU="
            );
            player.sendMessage("§aNPC gespawnt!");
            return true;
        }

        if (args[0].equalsIgnoreCase("despawn")) {
            npcManager.despawnNPC();
            player.sendMessage("§cNPC entfernt.");
            return true;
        }

        return true;
    }
}
