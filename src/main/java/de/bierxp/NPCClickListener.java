package de.bierxp;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class NPCClickListener implements Listener {

    private final NPCManager npcManager;

    public NPCClickListener(NPCManager npcManager) {
        [span_48](start_span)this.npcManager = npcManager;[span_48](end_span)
    }

    @EventHandler
    public void onNPCClick(PlayerInteractAtEntityEvent event) {
        [span_49](start_span)if (event.getHand() != EquipmentSlot.HAND) return;[span_49](end_span)
        [span_50](start_span)Entity clicked = event.getRightClicked();[span_50](end_span)

        if (npcManager.isNPC(clicked)) {
            [span_51](start_span)Player p = event.getPlayer();[span_51](end_span)
            [span_52](start_span)event.setCancelled(true);[span_52](end_span)
            
            [span_53](start_span)BierXP plugin = BierXP.getPlugin(BierXP.class);[span_53](end_span)
            [span_54](start_span)plugin.getXpHandler().openBankGUI(p);[span_54](end_span)
        }
    }
}
