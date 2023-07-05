package net.jadedmc.housing.listeners;

import net.jadedmc.housing.HousingPlugin;
import net.jadedmc.housing.houses.House;
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
        House house = plugin.houseManager().house(event.getPlayer().getWorld());

        if(house == null) {
            return;
        }

        plugin.houseManager().unload(house);
    }
}
