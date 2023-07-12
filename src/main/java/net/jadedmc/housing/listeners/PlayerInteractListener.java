package net.jadedmc.housing.listeners;

import net.jadedmc.housing.HousingPlugin;
import net.jadedmc.housing.guis.HousingMenuGUI;
import net.jadedmc.housing.houses.House;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {
    private final HousingPlugin plugin;

    public PlayerInteractListener(HousingPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the event is called.
     * @param event PlayerInteractEvent.
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        // Exit if the item is null.
        if(event.getItem() == null)
            return;

        // Exit if item meta is null.
        if(event.getItem().getItemMeta() == null)
            return;

        String item = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName());

        if(item == null) {
            return;
        }

        Player player = event.getPlayer();

        switch (item) {
            case "Housing Menu" -> {
                House house = plugin.houseManager().house(player.getWorld());

                if(house == null) {
                    new HousingMenuGUI(plugin, player).open(player);
                    return;
                }

                if(!house.isMember(player)) {
                    return;
                }

                new HousingMenuGUI(house, player).open(player);
            }
        }
    }
}