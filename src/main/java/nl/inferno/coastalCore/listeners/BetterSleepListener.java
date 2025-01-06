package nl.inferno.coastalCore.listeners;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BetterSleepListener implements Listener {

    private Set<UUID> sleepingPlayers = new HashSet<>();

    @EventHandler
    public void onPlayerSleep(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;

        World world = event.getPlayer().getWorld();
        sleepingPlayers.add(event.getPlayer().getUniqueId());

        int onlinePlayers = world.getPlayers().size();
        int requiredPlayers = Math.max(1, (int) Math.ceil(onlinePlayers * 0.25));
        int sleeping = sleepingPlayers.size();

        if (sleeping >= requiredPlayers) {
            world.setTime(0);

            Bukkit.broadcastMessage("§aEnough players are sleeping! Good morning!");
            sleepingPlayers.clear();
        } else {
            Bukkit.broadcastMessage("§e" + (requiredPlayers - sleeping) + " more players need to sleep to skip the night!");
        }
    }
    @EventHandler
    public void onPlayerLeaveBed(PlayerBedLeaveEvent event){
        sleepingPlayers.remove(event.getPlayer().getUniqueId());
    }
}
