package de.bierxp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

public class NPCManager {

    private Villager npc;

    public void spawnNPC(Location loc, String name, String value, String signature) {
        if (npc != null) despawnNPC();

        npc = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
        npc.setCustomNameVisible(true);
        npc.customName(Component.text(name).color(NamedTextColor.GOLD));
        npc.setAI(false);
        npc.setInvulnerable(true);
        npc.setProfession(Villager.Profession.LIBRARIAN);
        npc.setVillagerType(Villager.Type.PLAINS);
    }

    public void despawnNPC() {
        if (npc != null) {
            npc.remove();
            npc = null;
        }
    }

    public boolean isNPC(org.bukkit.entity.Entity e) {
        return npc != null && npc.getUniqueId().equals(e.getUniqueId());
    }
}
