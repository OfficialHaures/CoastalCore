package nl.inferno.coastalCore.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import nl.inferno.coastalCore.CoastalCore;
import java.util.HashMap;
import java.util.UUID;

public class TempFlyListener implements Listener {
    private final CoastalCore plugin;
    private static final HashMap<UUID, BukkitRunnable> activeFlyers = new HashMap<>();

    public TempFlyListener(CoastalCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
        if (!item.getItemMeta().getDisplayName().equals("§b§lTemporary Flight")) return;

        event.setCancelled(true);

        String durationStr = item.getItemMeta().getLore().get(1).split(": §e")[1];
        int minutes = Integer.parseInt(durationStr.split(" ")[0]);

        item.setAmount(item.getAmount() - 1);
        activateFlight(player, minutes);
    }

    private void activateFlight(Player player, int minutes) {
        if (activeFlyers.containsKey(player.getUniqueId())) {
            activeFlyers.get(player.getUniqueId()).cancel();
        }

        player.setAllowFlight(true);
        player.setFlying(true);
        player.sendMessage("§aFlight activated for " + minutes + " minutes!");

        BukkitRunnable runnable = new BukkitRunnable() {
            int timeLeft = minutes * 60;

            @Override
            public void run() {
                timeLeft--;

                if (timeLeft == 60) {
                    player.sendMessage("§c§lWarning: §eYour flight will expire in 1 minute!");
                }

                if (timeLeft <= 0) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.sendMessage("§cYour temporary flight has expired!");
                    activeFlyers.remove(player.getUniqueId());
                    this.cancel();
                }
            }
        };

        runnable.runTaskTimer(plugin, 20L, 20L);
        activeFlyers.put(player.getUniqueId(), runnable);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (activeFlyers.containsKey(player.getUniqueId())) {
            activeFlyers.get(player.getUniqueId()).cancel();
            activeFlyers.remove(player.getUniqueId());
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }

    public static void disableAllFlying() {
        for (BukkitRunnable runnable : activeFlyers.values()) {
            runnable.cancel();
        }
        activeFlyers.clear();
    }
}
