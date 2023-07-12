package net.jadedmc.housing.player;

import net.jadedmc.housing.HousingPlugin;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class HousingPlayerManager {
    private final HousingPlugin plugin;
    public Map<Player, HousingPlayer> players = new HashMap<>();

    public HousingPlayerManager(HousingPlugin plugin) {
        this.plugin = plugin;
    }

    public void addPlayer(Player player) {
        players.put(player, new HousingPlayer(plugin, player));
    }

    public HousingPlayer getPlayer(Player player) {
        return players.get(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }
}