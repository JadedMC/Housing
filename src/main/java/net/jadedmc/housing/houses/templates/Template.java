package net.jadedmc.housing.houses.templates;

import net.jadedmc.housing.HousingPlugin;
import org.bukkit.Material;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Template {
    private final HousingPlugin plugin;
    private final String templateID;
    private final String houseUUID;
    private String name = "Template";
    private Material icon = Material.BARRIER;
    private String spawn;

    public Template(HousingPlugin plugin, String templateID, String houseUUID) {
        this.plugin = plugin;
        this.templateID = templateID;
        this.houseUUID = houseUUID;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                {
                    PreparedStatement statement = plugin.mySQL().getConnection().prepareStatement("SELECT * FROM housing_houses WHERE houseUUID = ? LIMIT 1");
                    statement.setString(1, houseUUID);
                    ResultSet resultSet = statement.executeQuery();

                    if(resultSet.next()) {
                        spawn = resultSet.getString("spawn");
                    }
                }
                {
                    PreparedStatement statement = plugin.mySQL().getConnection().prepareStatement("SELECT * FROM housing_house_settings WHERE houseUUID = ? LIMIT 1");
                    statement.setString(1, houseUUID);
                    ResultSet resultSet = statement.executeQuery();

                    if(resultSet.next()) {
                        name = resultSet.getString("houseName");
                        icon = Material.valueOf(resultSet.getString("houseIcon"));
                    }
                }

            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public Material icon() {
        return icon;
    }

    public String id() {
        return templateID;
    }

    public InputStream inputStream() {
        try {
            PreparedStatement statement = plugin.mySQL().getConnection().prepareStatement("SELECT * FROM housing_house_files WHERE houseUUID = ? LIMIT 1");
            statement.setString(1, houseUUID);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                return resultSet.getBlob("houseFile").getBinaryStream();
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    public String name() {
        return name;
    }

    public String spawn() {
        return spawn;
    }
}
