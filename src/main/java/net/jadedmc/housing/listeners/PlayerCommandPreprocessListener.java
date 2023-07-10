package net.jadedmc.housing.listeners;

import net.jadedmc.housing.HousingPlugin;
import net.jadedmc.housing.houses.House;
import net.jadedmc.housing.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerCommandPreprocessListener implements Listener {
    private final HousingPlugin plugin;

    public PlayerCommandPreprocessListener(HousingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {

        // Makes sure the command is a WorldEdit command.
        if(!event.getMessage().contains("//")) {
            return;
        }

        Player player = event.getPlayer();
        House house = plugin.houseManager().house(player.getWorld());

        if(house == null) {
            if(player.hasPermission("housing.admin")) {
                return;
            }

            event.setCancelled(true);
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You can only use that command in your own house!");
            return;
        }

        if(!house.isMember(player)) {
            event.setCancelled(true);
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You can only use that command in your own house!");
            return;
        }
    }
}
