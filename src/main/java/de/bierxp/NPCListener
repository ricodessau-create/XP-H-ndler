package de.bierxp;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NPCListener extends PacketAdapter {

    private final int npcId;

    public NPCListener(int npcId) {
        super(Bukkit.getPluginManager().getPlugin("BierXP"), PacketType.Play.Client.USE_ENTITY);
        this.npcId = npcId;
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        int entity = event.getPacket().getIntegers().read(0);

        if (entity == npcId) {
            Player p = event.getPlayer();
            p.sendMessage("§eDu hast den NPC angeklickt!");
        }
    }
}
