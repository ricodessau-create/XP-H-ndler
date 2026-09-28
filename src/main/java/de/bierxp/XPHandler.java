package de.bierxp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class XPHandler implements Listener {

    private static final String GUI_TITLE = "Rico's XP-Bank";

    private final BierXP plugin;
    private final NamespacedKey xpBottleKey;

    public XPHandler(BierXP plugin) {
        this.plugin = plugin;
        this.xpBottleKey = new NamespacedKey(
                plugin,
                "stored_xp"
        );
    }

    public void openBankGUI(Player player) {
        Inventory inventory = Bukkit.createInventory(
                null,
                27,
                Component.text(GUI_TITLE)
                        .color(NamedTextColor.GOLD)
        );

        int balance = getBalance(
                player.getUniqueId()
        );

        int playerXP = getTotalExperience(
                player
        );

        inventory.setItem(
                1,
                createItem(
                        Material.ENCHANTED_BOOK,
                        "§aAlle XP einzahlen",
                        "§7Zahlt deine gesamten XP ein."
                )
        );

        inventory.setItem(
                2,
                createItem(
                        Material.ENCHANTED_BOOK,
                        "§a64 XP einzahlen",
                        "§7Zahlt 64 XP ein."
                )
        );

        inventory.setItem(
                3,
                createItem(
                        Material.ENCHANTED_BOOK,
                        "§a10 XP einzahlen",
                        "§7Zahlt 10 XP ein."
                )
        );

        inventory.setItem(
                4,
                createItem(
                        Material.ENCHANTED_BOOK,
                        "§a1 XP einzahlen",
                        "§7Zahlt 1 XP ein."
                )
        );

        inventory.setItem(
                13,
                createItem(
                        Material.NETHER_STAR,
                        "§eKontostand",
                        "§f" + balance + " XP gespeichert",
                        "§7Du trägst aktuell: §f" + playerXP + " XP"
                )
        );

        inventory.setItem(
                19,
                createItem(
                        Material.GOLD_NUGGET,
                        "§6Alle XP auszahlen",
                        "§7Zahlt dein gesamtes Guthaben aus."
                )
        );

        inventory.setItem(
                20,
                createItem(
                        Material.GOLD_NUGGET,
                        "§664 XP auszahlen",
                        "§7Zahlt 64 XP aus."
                )
        );

        inventory.setItem(
                21,
                createItem(
                        Material.GOLD_NUGGET,
                        "§610 XP auszahlen",
                        "§7Zahlt 10 XP aus."
                )
        );

        inventory.setItem(
                22,
                createItem(
                        Material.GOLD_NUGGET,
                        "§61 XP auszahlen",
                        "§7Zahlt 1 XP aus."
                )
        );

        inventory.setItem(
                23,
                createItem(
                        Material.EXPERIENCE_BOTTLE,
                        "§bAlle XP als Flaschen",
                        "§7Gibt dein gesamtes Bankguthaben",
                        "§7als spezielle XP-Flaschen aus."
                )
        );

        inventory.setItem(
                24,
                createItem(
                        Material.EXPERIENCE_BOTTLE,
                        "§b64 XP als Flasche",
                        "§7Gibt eine 64-XP-Flasche aus."
                )
        );

        inventory.setItem(
                25,
                createItem(
                        Material.EXPERIENCE_BOTTLE,
                        "§b10 XP als Flasche",
                        "§7Gibt eine 10-XP-Flasche aus."
                )
        );

        inventory.setItem(
                26,
                createItem(
                        Material.EXPERIENCE_BOTTLE,
                        "§b1 XP als Flasche",
                        "§7Gibt eine 1-XP-Flasche aus."
                )
        );

        player.openInventory(inventory);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(
            InventoryClickEvent event
    ) {
        if (!event.getView().title().equals(
                Component.text(GUI_TITLE)
                        .color(NamedTextColor.GOLD)
        )) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        ItemStack item = event.getCurrentItem();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return;
        }

        String name = meta.hasDisplayName()
                ? meta.getDisplayName()
                : "";

        UUID uuid = player.getUniqueId();

        if (item.getType() == Material.ENCHANTED_BOOK) {
            int playerXP = getTotalExperience(player);

            int amount;

            if (name.contains("Alle")) {
                amount = playerXP;
            } else if (name.contains("64")) {
                amount = 64;
            } else if (name.contains("10")) {
                amount = 10;
            } else {
                amount = 1;
            }

            if (amount <= 0) {
                player.sendMessage(
                        "§cDu hast keine XP."
                );
                return;
            }

            if (playerXP < amount) {
                player.sendMessage(
                        "§cDu hast nur §f"
                                + playerXP
                                + " §cXP."
                );
                return;
            }

            setTotalExperience(
                    player,
                    playerXP - amount
            );

            setBalance(
                    uuid,
                    getBalance(uuid) + amount
            );

            player.sendMessage(
                    "§a" + amount + " XP wurden eingezahlt."
            );

            player.playSound(
                    player.getLocation(),
                    Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                    1.0f,
                    1.0f
            );

            openBankGUI(player);
            return;
        }

        if (item.getType() == Material.GOLD_NUGGET) {
            int balance = getBalance(uuid);

            int amount;

            if (name.contains("Alle")) {
                amount = balance;
            } else if (name.contains("64")) {
                amount = 64;
            } else if (name.contains("10")) {
                amount = 10;
            } else {
                amount = 1;
            }

            if (amount <= 0) {
                player.sendMessage(
                        "§cDeine Bank ist leer."
                );
                return;
            }

            if (balance < amount) {
                player.sendMessage(
                        "§cDu hast nur §f"
                                + balance
                                + " §cXP §cin der Bank."
                );
                return;
            }

            setBalance(
                    uuid,
                    balance - amount
            );

            setTotalExperience(
                    player,
                    getTotalExperience(player) + amount
            );

            player.sendMessage(
                    "§6" + amount + " XP wurden ausgezahlt."
            );

            player.playSound(
                    player.getLocation(),
                    Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                    1.0f,
                    1.0f
            );

            openBankGUI(player);
            return;
        }

        if (item.getType() == Material.EXPERIENCE_BOTTLE) {
            int balance = getBalance(uuid);

            int amount;

            if (name.contains("Alle")) {
                amount = balance;
            } else if (name.contains("64")) {
                amount = 64;
            } else if (name.contains("10")) {
                amount = 10;
            } else {
                amount = 1;
            }

            if (amount <= 0) {
                player.sendMessage(
                        "§cDeine Bank ist leer."
                );
                return;
            }

            if (balance < amount) {
                player.sendMessage(
                        "§cNicht genug XP in der Bank."
                );
                return;
            }

            List<ItemStack> bottles = createXPBottles(
                    amount
            );

            int emptySlots = countEmptyInventorySlots(
                    player
            );

            if (emptySlots < bottles.size()) {
                player.sendMessage(
                        "§cDu brauchst mindestens §f"
                                + bottles.size()
                                + " §cfreie Inventarplätze."
                );
                return;
            }

            setBalance(
                    uuid,
                    balance - amount
            );

            for (ItemStack bottle : bottles) {
                player.getInventory().addItem(
                        bottle
                );
            }

            player.sendMessage(
                    "§b"
                            + amount
                            + " XP wurden als XP-Flaschen ausgezahlt."
            );

            player.playSound(
                    player.getLocation(),
                    Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                    1.0f,
                    1.0f
            );

            openBankGUI(player);
        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onXPBottleUse(
            PlayerInteractEvent event
    ) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR
                && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        EquipmentSlot hand = event.getHand();

        if (hand == null) {
            return;
        }

        ItemStack item = event.getItem();

        if (item == null
                || item.getType() != Material.EXPERIENCE_BOTTLE) {
            return;
        }

        Integer xpAmount = getStoredXP(item);

        if (xpAmount == null || xpAmount <= 0) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();

        consumeOneItem(
                player,
                hand
        );

        setTotalExperience(
                player,
                getTotalExperience(player) + xpAmount
        );

        player.sendMessage(
                "§b+" + xpAmount + " XP eingelöst."
        );

        player.playSound(
                player.getLocation(),
                Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
                1.0f,
                1.0f
        );
    }

    private int getBalance(UUID uuid) {
        return Math.max(
                0,
                plugin.getConfig().getInt(
                        "players." + uuid,
                        0
                )
        );
    }

    private void setBalance(
            UUID uuid,
            int amount
    ) {
        plugin.getConfig().set(
                "players." + uuid,
                Math.max(0, amount)
        );

        plugin.saveConfig();
    }

    public int getTotalExperience(
            Player player
    ) {
        int level = player.getLevel();

        if (level <= 0) {
            return Math.max(
                    0,
                    Math.round(
                            player.getExp()
                                    * getExpToNextLevel(0)
                    )
            );
        }

        int total = 0;

        for (int currentLevel = 0;
             currentLevel < level;
             currentLevel++) {

            total += getExpToNextLevel(
                    currentLevel
            );
        }

        total += Math.round(
                player.getExp()
                        * getExpToNextLevel(level)
        );

        return Math.max(
                0,
                total
        );
    }

    private void setTotalExperience(
            Player player,
            int experience
    ) {
        experience = Math.max(
                0,
                experience
        );

        player.setLevel(0);
        player.setExp(0.0f);
        player.setTotalExperience(0);

        if (experience > 0) {
            player.giveExp(experience);
        }
    }

    private int getExpToNextLevel(
            int level
    ) {
        if (level < 16) {
            return 2 * level + 7;
        }

        if (level < 31) {
            return 5 * level - 38;
        }

        return 9 * level - 158;
    }

    private ItemStack createItem(
            Material material,
            String name,
            String... lore
    ) {
        ItemStack item = new ItemStack(
                material
        );

        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        meta.setDisplayName(name);

        if (lore.length > 0) {
            meta.setLore(
                    List.of(lore)
            );
        }

        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createXPBottle(
            int xpAmount
    ) {
        ItemStack bottle = new ItemStack(
                Material.EXPERIENCE_BOTTLE
        );

        ItemMeta meta = bottle.getItemMeta();

        if (meta == null) {
            return bottle;
        }

        meta.setDisplayName(
                "§bXP-Flasche §f("
                        + xpAmount
                        + " XP)"
        );

        meta.setLore(
                List.of(
                        "§7Enthält: §f"
                                + xpAmount
                                + " XP",
                        "§7Rechtsklick zum Einlösen."
                )
        );

        meta.getPersistentDataContainer().set(
                xpBottleKey,
                PersistentDataType.INTEGER,
                xpAmount
        );

        bottle.setItemMeta(meta);

        return bottle;
    }

    private List<ItemStack> createXPBottles(
            int totalXP
    ) {
        List<ItemStack> bottles = new ArrayList<>();

        int remaining = totalXP;

        while (remaining > 0) {
            int amount = Math.min(
                    64,
                    remaining
            );

            bottles.add(
                    createXPBottle(amount)
            );

            remaining -= amount;
        }

        return bottles;
    }

    private Integer getStoredXP(
            ItemStack item
    ) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return null;
        }

        return meta.getPersistentDataContainer().get(
                xpBottleKey,
                PersistentDataType.INTEGER
        );
    }

    private void consumeOneItem(
            Player player,
            EquipmentSlot hand
    ) {
        if (hand == EquipmentSlot.HAND) {
            ItemStack item = player.getInventory()
                    .getItemInMainHand();

            if (item.getAmount() <= 1) {
                player.getInventory()
                        .setItemInMainHand(null);
            } else {
                item.setAmount(
                        item.getAmount() - 1
                );
            }

            return;
        }

        ItemStack item = player.getInventory()
                .getItemInOffHand();

        if (item.getAmount() <= 1) {
            player.getInventory()
                    .setItemInOffHand(null);
        } else {
            item.setAmount(
                    item.getAmount() - 1
            );
        }
    }

    private int countEmptyInventorySlots(
            Player player
    ) {
        int empty = 0;

        for (ItemStack item :
                player.getInventory().getStorageContents()) {

            if (item == null
                    || item.getType() == Material.AIR) {
                empty++;
            }
        }

        return empty;
    }
}
