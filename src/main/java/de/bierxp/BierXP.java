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

        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            getLogger().severe("ProtocolLib nicht gefunden! Plugin deaktiviert.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        npcManager = new NPCManager();
        xpHandler = new XPHandler(this);

        Bukkit.getPluginManager().registerEvents(new NPCClickListener(npcManager), this);
        Bukkit.getPluginManager().registerEvents(xpHandler, this);

        getCommand("bierxp").setExecutor(this);
        getLogger().info("BierXP erfolgreich geladen.");
    }

    public XPHandler getXpHandler() {
        return xpHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length == 0) {
            player.sendMessage("§eNutze: /bierxp <spawn|despawn>");
            return true;
        }

        if (args[0].equalsIgnoreCase("spawn")) {
            Location loc = player.getLocation().add(player.getLocation().getDirection().normalize().multiply(2));
            // Standardmäßig laden wir deinen Namen, der Skin folgt über das Profil
            npcManager.spawnNPC(loc, "RicoDessau", "", "");
            player.sendMessage("§aXP-Händler Rico gespawnt!");
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
