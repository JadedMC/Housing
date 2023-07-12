package net.jadedmc.housing.player;

import net.jadedmc.housing.HousingPlugin;
import net.jadedmc.housing.houses.HouseMetaData;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class HousingPlayer {
    private final HousingPlugin plugin;
    private final Player player;
    private final Map<String, HouseMetaData> houses = new HashMap<>();

    public HousingPlayer(HousingPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;

        // Load houses
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = plugin.mySQL().getConnection().prepareStatement("SELECT * FROM housing_houses WHERE ownerUUID = ?");
                statement.setString(1, player.getUniqueId().toString());
                ResultSet results = statement.executeQuery();

                while(results.next()) {
                    String houseUUID = results.getString("houseUUID");
                    houses.put(houseUUID, new HouseMetaData(plugin, houseUUID));
                }
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public Map<String, HouseMetaData> houses() {
        return houses;
    }
}