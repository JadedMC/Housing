package net.jadedmc.housing.houses.templates;

import net.jadedmc.housing.HousingPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the creation and loading of House Templates.
 */
public class TemplateManager {
    private final HousingPlugin plugin;
    private final Map<String, Template> loadedTemplates = new HashMap<>();

    /**
     * Creates the Template Manager.
     * @param plugin Instance of the plugin.
     */
    public TemplateManager(HousingPlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            // Keep trying until a connection is made.
            while(plugin.mySQL().getConnection() == null) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            // Loads each stored template.
            try {
                PreparedStatement statement = plugin.mySQL().getConnection().prepareStatement("SELECT * FROM housing_templates");
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    String templateID = resultSet.getString("templateID");
                    String houseUUID = resultSet.getString("houseUUID");
                    loadedTemplates.put(templateID, new Template(plugin, templateID, houseUUID));
                }
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Creates a template using an existing house.
     * @param templateID ID of the template.
     * @param houseUUID UUID of the house.
     */
    public void createTemplate(String templateID, String houseUUID) {
        loadedTemplates.put(templateID, new Template(plugin, templateID, houseUUID));

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = plugin.mySQL().getConnection().prepareStatement("REPLACE INTO housing_templates (templateID,houseUUID) VALUES (?,?)");
                statement.setString(1, templateID);
                statement.setString(2, houseUUID);
                statement.executeUpdate();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Get a template based off of its id.
     * Returns null if none found.
     * @param templateID ID of the template.
     * @return Corresponding template object.
     */
    public Template template(String templateID) {
        if(loadedTemplates.containsKey(templateID)) {
            return loadedTemplates.get(templateID);
        }

        return null;
    }

    /**
     * Get all currently loaded templates.
     * @return All templates.
     */
    public Collection<Template> templates() {
        return loadedTemplates.values();
    }
}