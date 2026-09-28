package de.bierxp;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class NPCClickListener implements Listener {

    private final BierXP plugin;
    private final NPCManager npcManager;

    public NPCClickListener(
            BierXP plugin,
            NPCManager npcManager
    ) {
        this.plugin = plugin;
        this.npcManager = npcManager;
    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onNPCRightClick(
            NPCRightClickEvent event
    ) {
        if (!npcManager.isNPC(event.getNPC())) {
            return;
        }

        Player player = event.getClicker();

        event.setCancelled(true);

        plugin.getXpHandler().openBankGUI(player);
    }
}
