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
        saveDefaultConfig();

        if (Bukkit.getPluginManager().getPlugin("Citizens") == null) {
            getLogger().severe("Citizens wurde nicht gefunden.");
            getLogger().severe("Bitte installiere Citizens 2 auf dem Server.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        npcManager = new NPCManager(this);
        xpHandler = new XPHandler(this);

        Bukkit.getPluginManager().registerEvents(
                new NPCClickListener(this, npcManager),
                this
        );

        Bukkit.getPluginManager().registerEvents(
                xpHandler,
                this
        );

        if (getCommand("bierxp") != null) {
            getCommand("bierxp").setExecutor(this);
        }

        npcManager.loadNPCDelayed();

        getLogger().info(
                "BierXP v" + getDescription().getVersion() + " erfolgreich geladen."
        );
    }

    @Override
    public void onDisable() {
        if (npcManager != null) {
            npcManager.saveNPCState();
        }
    }

    public XPHandler getXpHandler() {
        return xpHandler;
    }

    public NPCManager getNpcManager() {
        return npcManager;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(
                    "Dieser Befehl kann nur von einem Spieler verwendet werden."
            );
            return true;
        }

        if (!player.hasPermission("bierxp.admin")) {
            player.sendMessage("§cDazu hast du keine Berechtigung.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§eNutze:");
            player.sendMessage("§7/bierxp spawn");
            player.sendMessage("§7/bierxp despawn");
            return true;
        }

        if (args[0].equalsIgnoreCase("spawn")) {
            Location loc = player.getLocation().clone().add(
                    player.getLocation().getDirection().normalize().multiply(2)
            );

            if (npcManager.spawnNPC(loc)) {
                player.sendMessage(
                        "§aRico der XP-Dealer wurde gespawnt."
                );
            } else {
                player.sendMessage(
                        "§cDer XP-Dealer konnte nicht gespawnt werden."
                );
            }

            return true;
        }

        if (args[0].equalsIgnoreCase("despawn")) {
            if (npcManager.despawnNPC()) {
                player.sendMessage(
                        "§cRico der XP-Dealer wurde entfernt."
                );
            } else {
                player.sendMessage(
                        "§eDer XP-Dealer ist aktuell nicht gespawnt."
                );
            }

            return true;
        }

        player.sendMessage("§cUnbekannter Befehl.");
        player.sendMessage(
                "§eNutze: /bierxp <spawn|despawn>"
        );

        return true;
    }
}
