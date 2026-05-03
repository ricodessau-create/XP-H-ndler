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
        [span_0](start_span)// Sicherstellen, dass die Config existiert[span_0](end_span)
        saveDefaultConfig();

        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            [span_1](start_span)getLogger().severe("ProtocolLib nicht gefunden! Plugin deaktiviert.");[span_1](end_span)
            [span_2](start_span)Bukkit.getPluginManager().disablePlugin(this);[span_2](end_span)
            return;
        }

        npcManager = new NPCManager();
        [span_3](start_span)xpHandler = new XPHandler(this);[span_3](end_span)

        [span_4](start_span)Bukkit.getPluginManager().registerEvents(new NPCClickListener(npcManager), this);[span_4](end_span)
        [span_5](start_span)Bukkit.getPluginManager().registerEvents(xpHandler, this);[span_5](end_span)

        [span_6](start_span)getCommand("bierxp").setExecutor(this);[span_6](end_span)
        [span_7](start_span)getLogger().info("BierXP erfolgreich mit Speicher-Fix geladen.");[span_7](end_span)
    }

    public XPHandler getXpHandler() {
        [span_8](start_span)return xpHandler;[span_8](end_span)
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        [span_9](start_span)if (!(sender instanceof Player player)) return true;[span_9](end_span)

        if (args.length == 0) {
            [span_10](start_span)player.sendMessage("§eNutze: /bierxp <spawn|despawn>");[span_10](end_span)
            return true;
        }

        if (args[0].equalsIgnoreCase("spawn")) {
            Location loc = player.getLocation().add(player.getLocation().getDirection().normalize().multiply(2));
            // Hinweis: Trage hier deine echten Skin-Daten ein, damit der Skin dauerhaft bleibt
            [span_11](start_span)[span_12](start_span)npcManager.spawnNPC(loc, "RicoDessau", "DEIN_VALUE", "DEIN_SIGNATURE");[span_11](end_span)[span_12](end_span)
            [span_13](start_span)player.sendMessage("§aXP-Händler Rico gespawnt!");[span_13](end_span)
            return true;
        }

        if (args[0].equalsIgnoreCase("despawn")) {
            [span_14](start_span)npcManager.despawnNPC();[span_14](end_span)
            [span_15](start_span)player.sendMessage("§cNPC entfernt.");[span_15](end_span)
            return true;
        }

        return true;
    }
}
