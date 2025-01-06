package nl.inferno.coastalCore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import nl.inferno.coastalCore.CoastalCore;
import java.util.ArrayList;
import java.util.List;

public class TempFlyCommand implements CommandExecutor {
    private final CoastalCore plugin;

    public TempFlyCommand(CoastalCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("§cUsage: /tempfly <minutes>");
            return true;
        }

        try {
            int minutes = Integer.parseInt(args[0]);
            if (minutes <= 0) {
                sender.sendMessage("§cPlease specify a positive number of minutes!");
                return true;
            }

            Player player = (Player) sender;
            ItemStack feather = createFlyFeather(minutes);
            player.getInventory().addItem(feather);
            player.sendMessage("§aYou received a temporary fly feather for " + minutes + " minutes!");

        } catch (NumberFormatException e) {
            sender.sendMessage("§cPlease specify a valid number of minutes!");
        }

        return true;
    }

    private ItemStack createFlyFeather(int minutes) {
        ItemStack feather = new ItemStack(Material.FEATHER);
        ItemMeta meta = feather.getItemMeta();
        meta.setDisplayName("§b§lTemporary Flight");

        List<String> lore = new ArrayList<>();
        lore.add("§7Right-click to activate");
        lore.add("§7Duration: §e" + minutes + " minutes");
        lore.add("§7Status: §aUnused");
        meta.setLore(lore);

        feather.setItemMeta(meta);
        return feather;
    }
}
