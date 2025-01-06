package nl.inferno.coastalCore.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ShoulderRideListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) return;
        if (!event.getPlayer().isSneaking()) return;

        Player rider = event.getPlayer();
        Player mount = (Player) event.getRightClicked();

        if (mount.getPassengers().isEmpty()) {
            mount.addPassenger(rider);
            rider.sendMessage("§aYou're now riding on " + mount.getName() + "'s shoulders!");
            mount.sendMessage("§a" + rider.getName() + " is now riding on your shoulders!");
        }
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (player.getVehicle() instanceof Player) {
            event.setCancelled(true);
            openDismountMenu(player);
        }
    }

    private void openDismountMenu(Player rider) {
        Inventory menu = Bukkit.createInventory(null, 9, "§8Dismount Menu");

        ItemStack dismountItem = new ItemStack(Material.FEATHER);
        ItemMeta meta = dismountItem.getItemMeta();
        meta.setDisplayName("§cGet off shoulders");
        meta.setLore(Arrays.asList(
                "§7Click to dismount from",
                "§7the player's shoulders",
                "",
                "§eClick to dismount!"
        ));
        dismountItem.setItemMeta(meta);

        menu.setItem(4, dismountItem);
        rider.openInventory(menu);
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("§8Dismount Menu")) return;
        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() != Material.FEATHER) return;

        Player rider = (Player) event.getWhoClicked();
        if (rider.getVehicle() instanceof Player) {
            rider.getVehicle().removePassenger(rider);
            rider.closeInventory();
            rider.sendMessage("§aYou got off the player's shoulders!");
        }
    }
}
