package net.jadedmc.housing.houses;

import net.jadedmc.housing.HousingPlugin;
import org.bukkit.Material;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents visual data of a house.
 * Used when browsing houses.
 */
public class HouseMetaData {
    private final HousingPlugin plugin;
    private final String uuid;
    private Material icon = Material.CRAFTING_TABLE;
    private String name = "House";


    /**
     * Creates the HouseMetaData object.
     * @param plugin Instance of the plugin.
     * @param uuid UUID of the house.
     */
    public HouseMetaData(HousingPlugin plugin, String uuid) {
        this.plugin = plugin;
        this.uuid = uuid;

        // Load data:
        update();
    }

    /**
     * Get the icon material of the house.
     * @return House icon material.
     */
    public Material icon() {
        return icon;
    }

    /**
     * Gets the name of the house.
     * @return House name.
     */
    public String name() {
        return name;
    }

    /**
     * Updates the data. Useful whenever a house is edited.
     */
    public void update() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = plugin.mySQL().getConnection().prepareStatement("SELECT * FROM housing_house_settings WHERE houseUUID = ? LIMIT 1 ");
                statement.setString(1, uuid);
                ResultSet results = statement.executeQuery();

                if(results.next()) {
                    name = results.getString("houseName");
                    icon = Material.valueOf(results.getString("houseIcon"));
                }
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Gets the UUID of the house.
     * @return UUID uuid.
     */
    public String uuid() {
        return uuid;
    }
}