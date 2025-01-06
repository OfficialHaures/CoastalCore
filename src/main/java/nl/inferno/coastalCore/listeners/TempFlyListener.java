package nl.inferno.coastalCore.listeners;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
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
    private static final HashMap<UUID, BossBar> playerBossBars = new HashMap<>();

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
            if (playerBossBars.containsKey(player.getUniqueId())) {
                playerBossBars.get(player.getUniqueId()).removeAll();
                playerBossBars.remove(player.getUniqueId());
            }
        }

        player.setAllowFlight(true);
        player.setFlying(true);
        player.sendMessage("§aFlight activated for " + minutes + " minutes!");

        // Create BossBar
        BossBar bossBar = Bukkit.createBossBar(
            "§bFlight Time Remaining: " + minutes + ":00",
            BarColor.BLUE,
            BarStyle.SOLID
        );
        bossBar.addPlayer(player);
        playerBossBars.put(player.getUniqueId(), bossBar);

        int totalSeconds = minutes * 60;
        BukkitRunnable runnable = new BukkitRunnable() {
            int timeLeft = totalSeconds;

            @Override
            public void run() {
                timeLeft--;

                int minutesLeft = timeLeft / 60;
                int secondsLeft = timeLeft % 60;
                String timeString = String.format("§bFlight Time Remaining: %d:%02d", minutesLeft, secondsLeft);
                bossBar.setTitle(timeString);
                bossBar.setProgress((double) timeLeft / totalSeconds);

                if (timeLeft == 60) {
                    player.sendMessage("§c§lWarning: §eYour flight will expire in 1 minute!");
                }

                if (timeLeft <= 0) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.sendMessage("§cYour temporary flight has expired!");
                    bossBar.removeAll();
                    playerBossBars.remove(player.getUniqueId());
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
        UUID uuid = player.getUniqueId();

        if (activeFlyers.containsKey(uuid)) {
            activeFlyers.get(uuid).cancel();
            activeFlyers.remove(uuid);

            if (playerBossBars.containsKey(uuid)) {
                playerBossBars.get(uuid).removeAll();
                playerBossBars.remove(uuid);
            }

            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }

    public static void disableAllFlying() {
        for (BukkitRunnable runnable : activeFlyers.values()) {
            runnable.cancel();
        }
        for (BossBar bossBar : playerBossBars.values()) {
            bossBar.removeAll();
        }
        activeFlyers.clear();
        playerBossBars.clear();
    }
}