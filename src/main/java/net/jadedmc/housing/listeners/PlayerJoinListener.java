package net.jadedmc.housing.listeners;

import net.jadedmc.housing.HousingPlugin;
import net.jadedmc.housing.utils.LocationUtils;
import net.jadedmc.housing.utils.item.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final HousingPlugin plugin;

    public PlayerJoinListener(HousingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.housingPlayerManager().addPlayer(player);

        // Teleport player to spawn if it is set.
        if(plugin.settingsManager().getConfig().getBoolean("Spawn.Set")) {
            player.teleport(LocationUtils.getSpawn(plugin));
        }

        // Reset player to lobby status
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();
        player.getInventory().setItem(4, new ItemBuilder(Material.NETHER_STAR).setDisplayName("&a&lHousing Menu").build());
    }
}
