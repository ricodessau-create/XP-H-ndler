package de.bierxp;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BierXP extends JavaPlugin {

    private NPCManager npcManager;

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            getLogger().severe("ProtocolLib nicht gefunden! Plugin wird deaktiviert.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.npcManager = new NPCManager();
        getLogger().info("BierXP mit Packet-NPCs gestartet.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("spawnnpc")) return false;
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Nur Ingame nutzbar.");
            return true;
        }

        // Beispiel-Skin (muss durch deinen echten Value/Signature ersetzt werden)
        String skinValue = "DEIN_TEXTURE_VALUE_HIER";
        String skinSignature = "DEINE_SIGNATURE_HIER";

        Location loc = player.getLocation().add(player.getLocation().getDirection().normalize().multiply(2));
        npcManager.spawnNPC(loc, "BierNPC", skinValue, skinSignature);

        player.sendMessage("§aNPC gespawnt.");
        return true;
    }
}
