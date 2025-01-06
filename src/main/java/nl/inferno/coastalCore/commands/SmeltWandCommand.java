package nl.inferno.coastalCore.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SmeltWandCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("§cUsage: /smeltwand <uses>");
            return true;
        }

        try {
            int uses = Integer.parseInt(args[0]);
            if (uses <= 0) {
                sender.sendMessage("§cPlease specify a positive number of uses!");
                return true;
            }

            Player player = (Player) sender;
            ItemStack wand = createSmeltWand(uses);
            player.getInventory().addItem(wand);
            player.sendMessage("§aYou received a Smelt Wand with " + uses + " uses!");

        } catch (NumberFormatException e) {
            sender.sendMessage("§cPlease specify a valid number!");
        }

        return true;
    }

    private ItemStack createSmeltWand(int uses) {
        ItemStack wand = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = wand.getItemMeta();
        meta.setDisplayName("§6§lSmelt Wand");

        List<String> lore = new ArrayList<>();
        lore.add("§7Right-click a chest to smelt its contents");
        lore.add("§7Remaining uses: §e" + uses);
        meta.setLore(lore);

        wand.setItemMeta(meta);
        return wand;
    }
}
