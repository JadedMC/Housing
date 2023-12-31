/*
 * This file is part of JadedChat, licensed under the MIT License.
 *
 *  Copyright (c) JadedMC
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package net.jadedmc.housing.houses;

import net.jadedmc.housing.HousingPlugin;
import net.jadedmc.housing.houses.generator.EmptyChunkGenerator;
import net.jadedmc.housing.houses.templates.Template;
import net.jadedmc.housing.utils.LocationUtils;
import net.jadedmc.housing.utils.item.ItemBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Represents a player's private world.
 */
public class House {
    private final HousingPlugin plugin;
    private final UUID houseUUID;
    private World world;
    private String ownerUUID;
    private boolean loaded = false;
    private boolean saved = false;
    private String spawn;
    
    
    // House Settings
    private GameMode defaultGameMode = GameMode.CREATIVE;
    private Material icon = Material.CRAFTING_TABLE;
    private String name = "House";
    private boolean pvp = false;
    private long time = 6000;
    private HouseVisibility visibility = HouseVisibility.PUBLIC;
    private HouseWeather weather = HouseWeather.CLEAR;

    /**
     * Creates a new house using a specified template.
     * @param plugin Instance of the plugin.
     * @param templateID ID of the template being used.
     * @param owner Owner of the house.
     */
    public House(HousingPlugin plugin, String templateID, Player owner) {
        this.plugin = plugin;
        this.houseUUID = UUID.randomUUID();
        this.ownerUUID = owner.getUniqueId().toString();
        this.name = owner.getName() + "'s House";

        Template template = plugin.templateManager().template(templateID);
        this.spawn = template.spawn();
        WorldCreator tempWorldCreator = new WorldCreator(houseUUID.toString());
        tempWorldCreator.generator(new EmptyChunkGenerator(spawn));
        World tempWorld = tempWorldCreator.createWorld();
        assert tempWorld != null;
        Bukkit.unloadWorld(tempWorld, false);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                // Save the file to the server.
                InputStream inputStream = template.inputStream();
                File regionFolder = new File(tempWorld.getWorldFolder(), "region");
                File regionFile = new File(regionFolder, "r.0.0.mca");
                Files.copy(inputStream, regionFile.toPath());

                // Create and load the world.
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    // Create the world.
                    WorldCreator worldCreator = new WorldCreator(houseUUID.toString());
                    worldCreator.generator(new EmptyChunkGenerator(spawn));
                    world = worldCreator.createWorld();

                    // Set game rules.
                    assert world != null;
                    world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                    world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
                    world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
                    world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);

                    // Set time and weather.
                    world.setStorm(false);
                    world.setTime(6000);

                    // Set world border.
                    WorldBorder worldBorder = world.getWorldBorder();
                    worldBorder.setCenter(new Location(world, 256, 100, 256));
                    worldBorder.setSize(512);

                    this.loaded = true;
                });
            }
            catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }

    public House(HousingPlugin plugin, UUID houseUUID) {
        this.plugin = plugin;
        this.houseUUID = houseUUID;

        WorldCreator tempWorldCreator = new WorldCreator(houseUUID.toString());
        tempWorldCreator.generator(new EmptyChunkGenerator());
        World tempWorld = tempWorldCreator.createWorld();
        assert tempWorld != null;
        Bukkit.unloadWorld(tempWorld, false);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                {
                    PreparedStatement statement = plugin.mySQL().getConnection().prepareStatement("SELECT * FROM housing_houses WHERE houseUUID = ? LIMIT 1");
                    statement.setString(1, houseUUID.toString());
                    ResultSet resultSet = statement.executeQuery();

                    if(resultSet.next()) {
                        ownerUUID = resultSet.getString("ownerUUID");
                        spawn = resultSet.getString("spawn");
                    }
                }

                {
                    PreparedStatement statement = plugin.mySQL().getConnection().prepareStatement("SELECT * FROM housing_house_settings WHERE houseUUID = ? LIMIT 1");
                    statement.setString(1, houseUUID.toString());
                    ResultSet resultSet = statement.executeQuery();

                    if(resultSet.next()) {
                        name = resultSet.getString("houseName");
                        icon = Material.valueOf(resultSet.getString("houseIcon"));
                        time = resultSet.getLong("time");
                        defaultGameMode = GameMode.valueOf(resultSet.getString("gameMode"));
                        pvp = resultSet.getBoolean("pvp");
                        visibility = HouseVisibility.valueOf(resultSet.getString("visibility"));
                        weather = HouseWeather.valueOf(resultSet.getString("weather"));
                    }
                }

                {
                    PreparedStatement statement = plugin.mySQL().getConnection().prepareStatement("SELECT * FROM housing_house_files WHERE houseUUID = ? LIMIT 1");
                    statement.setString(1, houseUUID.toString());
                    ResultSet resultSet = statement.executeQuery();

                    if(resultSet.next()) {
                        // Get the region file from MySQL.
                        Blob blob = resultSet.getBlob("houseFile");
                        InputStream inputStream = blob.getBinaryStream();

                        // Save the file to the server.
                        File regionFolder = new File(tempWorld.getWorldFolder(), "region");
                        File regionFile = new File(regionFolder, "r.0.0.mca");
                        Files.copy(inputStream, regionFile.toPath());

                        // Create and load the world.
                        plugin.getServer().getScheduler().runTask(plugin, () -> {
                            // Create the world.
                            WorldCreator worldCreator = new WorldCreator(houseUUID.toString());
                            worldCreator.generator(new EmptyChunkGenerator(spawn));
                            world = worldCreator.createWorld();

                            // Set game rules.
                            assert world != null;
                            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
                            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
                            world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);

                            // Set time and weather.
                            world.setStorm(false);
                            world.setTime(time);

                            // Set world border.
                            WorldBorder worldBorder = world.getWorldBorder();
                            worldBorder.setCenter(new Location(world, 256, 100, 256));
                            worldBorder.setSize(512);
                        });
                    }
                }

                // Load house entities.
                {
                    PreparedStatement statement = plugin.mySQL().getConnection().prepareStatement("SELECT * FROM housing_house_entities WHERE houseUUID = ? LIMIT 1");
                    statement.setString(1, houseUUID.toString());
                    ResultSet resultSet = statement.executeQuery();

                    if(resultSet.next()) {
                        // Get the entities file from MySQL.
                        Blob blob = resultSet.getBlob("entitiesFile");
                        InputStream inputStream = blob.getBinaryStream();

                        // Save the file to the server.
                        File entitiesFolder = new File(tempWorld.getWorldFolder(), "entities");
                        File entitiesFile = new File(entitiesFolder, "r.0.0.mca");
                        Files.copy(inputStream, entitiesFile.toPath());
                    }
                }

                this.loaded = true;
            }
            catch (SQLException | IOException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Gets the icon of the house.
     * @return House icon.
     */
    public Material icon() {
        return icon;
    }

    /**
     * Check if a player is a member of the house.
     * Returns true for both members and the owner.
     * @param player Player to check.
     * @return Whether the player is a house member.
     */
    public boolean isMember(Player player) {
        // Gives staff members permission to use staff houses.
        if(ownerUUID.equalsIgnoreCase("JadedMC") && player.hasPermission("housing.admin")) {
            return true;
        }

        if(player.getUniqueId().toString().equals(ownerUUID)) {
            return true;
        }

        return false;
    }

    public void save() {
        world.save();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                File regionFolder = new File(world.getWorldFolder(), "region");
                File regionFile = new File(regionFolder, "r.0.0.mca");
                InputStream inputStream = Files.newInputStream(regionFile.toPath());

                // Saves the house default data.
                {
                    PreparedStatement statement = plugin.mySQL().getConnection().prepareStatement("REPLACE INTO housing_houses (houseUUID,ownerUUID,spawn) VALUES (?,?,?)");
                    statement.setString(1, houseUUID.toString());
                    statement.setString(2, ownerUUID.toString());
                    statement.setString(3, this.spawn);
                    statement.executeUpdate();
                }

                // Saves the house world file.
                {
                    PreparedStatement statement = plugin.mySQL().getConnection().prepareStatement("REPLACE INTO housing_house_files (houseUUID,houseFile) VALUES (?,?)");
                    statement.setString(1, houseUUID.toString());
                    statement.setBlob(2, inputStream);
                    statement.executeUpdate();
                }

                // Saves the entities file.
                {
                    File entitiesFolder = new File(world.getWorldFolder(), "entities");
                    File entitiesFile = new File(entitiesFolder, "r.0.0.mca");

                    if(entitiesFile.exists()) {
                        InputStream entitiesInputStream = Files.newInputStream(entitiesFile.toPath());

                        PreparedStatement statement = plugin.mySQL().getConnection().prepareStatement("REPLACE INTO housing_house_entities (houseUUID,entitiesFile) VALUES (?,?)");
                        statement.setString(1, houseUUID.toString());
                        statement.setBlob(2, entitiesInputStream);
                        statement.executeUpdate();
                    }
                }

                // Saves the house settings.
                {
                    PreparedStatement statement = plugin.mySQL().getConnection().prepareStatement("REPLACE INTO housing_house_settings (houseUUID,houseName,houseIcon,visibility,weather,time,gameMode,pvp) VALUES (?,?,?,?,?,?,?,?)");
                    statement.setString(1, houseUUID.toString());
                    statement.setString(2, name);
                    statement.setString(3, icon.toString());
                    statement.setString(4, visibility.toString());
                    statement.setString(5, weather.toString());
                    statement.setLong(6, world.getTime());
                    statement.setString(7, defaultGameMode.toString());
                    statement.setBoolean(8, pvp);
                    statement.executeUpdate();
                }

                saved = true;
            }
            catch (IOException | SQLException exception) {
                exception.printStackTrace();
            }

        });
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public Location spawnLocation() {
        String[] location = spawn.split(",");
        double x = Double.parseDouble(location[0]);
        double y = Double.parseDouble(location[1]);
        double z = Double.parseDouble(location[2]);
        float yaw = (float) Double.parseDouble(location[3]);
        float pitch = (float) Double.parseDouble(location[4]);

        return new Location(world, x, y, z, yaw, pitch);
    }

    public void removePlayer(Player player) {
        if(world.getPlayers().size() == 1) {
            plugin.houseManager().unload(this);
            return;
        }

        if(plugin.settingsManager().getConfig().getBoolean("Spawn.Set")) {
            player.teleport(LocationUtils.getSpawn(plugin));
        }
        else {
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }

        player.setGameMode(GameMode.ADVENTURE);
        player.setMaxHealth(20);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setTotalExperience(0);
        player.getInventory().clear();
        player.getInventory().setItem(4, new ItemBuilder(Material.NETHER_STAR).setDisplayName("&a&lHousing Menu").build());
    }

    /**
     * Transfer's the house to a new owner.
     * @param newOwnerUUID New owner's uuid,
     */
    public void transfer(String newOwnerUUID) {
        ownerUUID = newOwnerUUID;
    }

    public void unload() {
        // Force all remaining players to leave.
        world.getPlayers().forEach(player -> {
            if(plugin.settingsManager().getConfig().getBoolean("Spawn.Set")) {
                player.teleport(LocationUtils.getSpawn(plugin));
            }
            else {
                player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            }
        });

        // Unloads the world.
        plugin.getServer().unloadWorld(world, false);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            // Wait until the house is saved.
            while(!saved) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            // Delete the world folder.
            try {
                FileUtils.deleteDirectory(world.getWorldFolder());
            }
            catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }

    public UUID uuid() {
        return houseUUID;
    }

    public void visit(Player player) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            while(!loaded) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                player.teleport(spawnLocation());
                player.setGameMode(GameMode.CREATIVE);

                player.getInventory().clear();
                player.getInventory().setItem(8, new ItemBuilder(Material.NETHER_STAR).setDisplayName("&a&lHousing Menu").build());
            });
        });
    }

    public World world() {
        return world;
    }
}