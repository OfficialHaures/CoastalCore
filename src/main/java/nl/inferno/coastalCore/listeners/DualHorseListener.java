package nl.inferno.coastalCore.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;


public class DualHorseListener implements Listener {

    @EventHandler
    public void onHorseInteract(PlayerInteractEntityEvent event){
        Entity entity = event.getRightClicked();
        if(!(entity instanceof Horse)) return;

        Horse horse = (Horse) entity;
        Player player = event.getPlayer();

        if(horse.getPassengers().size() == 1 && !horse.getPassengers().contains(player)){
            horse.addPassenger(player);
            player.sendMessage("§aYou hopped on the horse with another player!");
        }
    }
}
