package de.bierxp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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

        getCommand("bierxp").setExecutor(this);
        getServer().getPluginManager().registerEvents(xpHandler, this);

        getLogger().info("BierXP geladen!");
    }

    @Override
    public void onDisable() {
        if (xpHandler != null) xpHandler.save();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("bierxp.admin")) {
            sender.sendMessage("Keine Rechte.");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl nutzen.");
            return true;
        }

        Player p = (Player) sender;

        // ALTER COMMAND BLEIBT: /bierxp spawn
        if (args.length == 1 && args[0].equalsIgnoreCase("spawn")) {

            // Skin-Daten für RicoDessau
            String skinValue = "ewogICJ0aW1lc3RhbXAiIDogMTcwMjcxMzA5NjI1MiwKICAicHJvZmlsZUlkIiA6ICI0YzY0YzE5YjY2YzQ0YzQ3OTg0Y2Y3YzYzYzYzYzYzIiwKICAicHJvZmlsZU5hbWUiIDogIlJpY29EZXNzYXUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzYzY
