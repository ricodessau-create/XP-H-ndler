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
        // NPCs müssen nicht gespeichert werden, da Fake-Player nicht persistieren
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

        // /bierxp spawnnpc
        if (args.length == 1 && args[0].equalsIgnoreCase("spawnnpc")) {

            // Beispiel-Skin (muss später ersetzt werden)
            String skinValue = "SKIN_VALUE_HIER";
            String skinSignature = "SKIN_SIGNATURE_HIER";

            npcManager.spawnNPC(
                    p.getLocation(),
                    "BierXP Banker",
                    skinValue,
                    skinSignature
            );

            p.sendMessage("NPC gespawnt.");
            return true;
        }

        p.sendMessage("Nutze: /bierxp spawnnpc");
        return true;
    }

    public static BierXP getInstance() { return instance; }
    public NPCManager getNPCManager() { return npcManager; }
    public XPHandler getXPHandler() { return xpHandler; }
}
