package net.jadedmc.housing.listeners;

import net.jadedmc.housing.HousingPlugin;
import net.jadedmc.housing.houses.House;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {
    private final HousingPlugin plugin;

    public BlockPlaceListener(HousingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        House house = plugin.houseManager().house(player.getWorld());

        // Allow admins to bypass building restrictions.
        if(player.hasPermission("housing.admin")) {
            return;
        }

        // Prevent building in the spawn if the player does not have permission.
        if(house == null) {
            event.setCancelled(true);
            return;
        }

        // Prevent non-members from building in the house.
        if(!house.isMember(player)) {
            event.setCancelled(true);
        }
    }
}