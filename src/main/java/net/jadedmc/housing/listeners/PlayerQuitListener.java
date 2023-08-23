package net.jadedmc.housing.listeners;

import net.jadedmc.housing.HousingPlugin;
import net.jadedmc.housing.houses.House;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final HousingPlugin plugin;

    public PlayerQuitListener(HousingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.housingPlayerManager().removePlayer(player);
        House house = plugin.houseManager().house(player.getWorld());

        if(house == null) {
            return;
        }

        house.removePlayer(player);
    }
}