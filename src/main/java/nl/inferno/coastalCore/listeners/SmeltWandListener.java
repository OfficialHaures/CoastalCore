package nl.inferno.coastalCore.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmeltWandListener implements Listener {

    private final Map<Material, Material> smeltableItems = new HashMap<>();

    public SmeltWandListener() {
        smeltableItems.put(Material.IRON_ORE, Material.IRON_INGOT);
        smeltableItems.put(Material.GOLD_ORE, Material.GOLD_INGOT);
        smeltableItems.put(Material.COPPER_ORE, Material.COPPER_INGOT);
        smeltableItems.put(Material.RAW_IRON, Material.IRON_INGOT);
        smeltableItems.put(Material.RAW_GOLD, Material.GOLD_INGOT);
        smeltableItems.put(Material.RAW_COPPER, Material.COPPER_INGOT);
        smeltableItems.put(Material.SAND, Material.GLASS);
        smeltableItems.put(Material.COBBLESTONE, Material.STONE);
        smeltableItems.put(Material.CLAY_BALL, Material.BRICK);
        smeltableItems.put(Material.NETHERRACK, Material.NETHER_BRICK);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        Block block = event.getClickedBlock();

        if (block == null || !(block.getState() instanceof Chest)) return;
        if (!isSmeltWand(item)) return;

        event.setCancelled(true);

        int uses = getRemainingUses(item);
        if (uses <= 0) {
            player.getInventory().setItemInMainHand(null);
            player.sendMessage("§cYour Smelt Wand has run out of uses!");
            return;
        }

        Chest chest = (Chest) block.getState();
        boolean smeltedAny = smeltChestContents(chest);

        if (smeltedAny) {
            uses--;
            updateWandUses(item, uses);
            if (uses <= 0) {
                player.getInventory().setItemInMainHand(null);
                player.sendMessage("§cYour Smelt Wand has run out of uses!");
            } else {
                player.sendMessage("§aSuccessfully smelted chest contents! §7(" + uses + " uses remaining)");
            }
        } else {
            player.sendMessage("§cNo smeltable items found in chest!");
        }
    }

    private boolean isSmeltWand(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName().equals("§6§lSmelt Wand");
    }

    private int getRemainingUses(ItemStack wand) {
        List<String> lore = wand.getItemMeta().getLore();
        String usesLine = lore.get(1);
        return Integer.parseInt(usesLine.split("§e")[1]);
    }

    private void updateWandUses(ItemStack wand, int uses) {
        ItemMeta meta = wand.getItemMeta();
        List<String> lore = meta.getLore();
        lore.set(1, "§7Remaining uses: §e" + uses);
        meta.setLore(lore);
        wand.setItemMeta(meta);
    }

    private boolean smeltChestContents(Chest chest) {
        boolean smeltedAny = false;

        for (ItemStack item : chest.getInventory().getContents()) {
            if (item == null) continue;

            Material smeltedMaterial = smeltableItems.get(item.getType());
            if (smeltedMaterial != null) {
                ItemStack smeltedItem = new ItemStack(smeltedMaterial, item.getAmount());
                chest.getInventory().remove(item);
                chest.getInventory().addItem(smeltedItem);
                smeltedAny = true;
            }
        }

        return smeltedAny;
    }
}