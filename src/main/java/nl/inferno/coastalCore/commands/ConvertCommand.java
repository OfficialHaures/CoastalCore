package nl.inferno.coastalCore.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

public class ConvertCommand implements CommandExecutor {
    private static final Map<Material, Material> CONVERTIBLE_ITEMS = new HashMap<>();

    static {
        CONVERTIBLE_ITEMS.put(Material.IRON_INGOT, Material.IRON_BLOCK);
        CONVERTIBLE_ITEMS.put(Material.GOLD_INGOT, Material.GOLD_BLOCK);
        CONVERTIBLE_ITEMS.put(Material.DIAMOND, Material.DIAMOND_BLOCK);
        CONVERTIBLE_ITEMS.put(Material.EMERALD, Material.EMERALD_BLOCK);
    }

    private final boolean convert;

    public ConvertCommand(boolean convert) {
        this.convert = convert;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        PlayerInventory inventory = player.getInventory();

        if (convert) {
            convertItems(player, inventory);
        } else {
            unconvertItems(player, inventory);
        }

        return true;
    }

    private void convertItems(Player player, PlayerInventory inventory) {
        for (Map.Entry<Material, Material> entry : CONVERTIBLE_ITEMS.entrySet()) {
            Material from = entry.getKey();
            Material to = entry.getValue();

            int amount = getAmount(inventory, from);
            if (amount >= 9) {
                int blocks = amount / 9;
                removeItems(inventory, from, blocks * 9);
                inventory.addItem(new ItemStack(to, blocks));
                player.sendMessage("§aConverted " + (blocks * 9) + " " + from.name() + " into " + blocks + " " + to.name());
            }
        }
    }

    private void unconvertItems(Player player, PlayerInventory inventory) {
        for (Map.Entry<Material, Material> entry : CONVERTIBLE_ITEMS.entrySet()) {
            Material to = entry.getKey();
            Material from = entry.getValue();

            int amount = getAmount(inventory, from);
            if (amount > 0) {
                removeItems(inventory, from, amount);
                inventory.addItem(new ItemStack(to, amount * 9));
                player.sendMessage("§aConverted " + amount + " " + from.name() + " into " + (amount * 9) + " " + to.name());
            }
        }
    }

    private int getAmount(PlayerInventory inventory, Material material) {
        int amount = 0;
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == material) {
                amount += item.getAmount();
            }
        }
        return amount;
    }

    private void removeItems(PlayerInventory inventory, Material material, int amount) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.getType() == material) {
                if (item.getAmount() <= amount) {
                    amount -= item.getAmount();
                    inventory.setItem(i, null);
                } else {
                    item.setAmount(item.getAmount() - amount);
                    break;
                }
            }
        }
    }
}
