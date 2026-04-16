package de.bierxp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BierXP extends JavaPlugin implements CommandExecutor {

    private static BierXP instance;
    private BankerManager bankerManager;
    private XPHandler xpHandler;

    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        
        bankerManager = new BankerManager(this);
        xpHandler = new XPHandler(this);

        getCommand("bierxp").setExecutor(this);
        getServer().getPluginManager().registerEvents(xpHandler, this);

        getLogger().info("BierXP geladen!");
    }

    @Override
    public void onDisable() {
        if (bankerManager != null) bankerManager.save();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("bierxp.admin")) {
            sender.sendMessage("Keine Rechte.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("spawn")) {
            if (!(sender instanceof Player)) return true;
            Player p = (Player) sender;
            bankerManager.spawnBanker(p.getLocation());
            p.sendMessage("Bankier gespawnt.");
            return true;
        }

        sender.sendMessage("Nutze: /bierxp spawn");
        return true;
    }

    public static BierXP getInstance() { return instance; }
    public BankerManager getBankerManager() { return bankerManager; }
    public XPHandler getXPHandler() { return xpHandler; }
}
