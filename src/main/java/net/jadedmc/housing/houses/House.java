package net.jadedmc.housing.houses;

import net.jadedmc.housing.HousingPlugin;
import net.jadedmc.housing.houses.generator.EmptyChunkGenerator;
import net.jadedmc.housing.houses.templates.Template;
import net.jadedmc.housing.utils.LocationUtils;
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

public class House {
    private final HousingPlugin plugin;
    private final UUID houseUUID;
    private World world;
    private String ownerUUID;
    private boolean loaded = false;
    private boolean saved = false;
    private String spawn;

    public House(HousingPlugin plugin, String templateID, Player owner) {
        this.plugin = plugin;
        this.houseUUID = UUID.randomUUID();
        this.ownerUUID = owner.getUniqueId().toString();

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
                            world.setTime(6000);

                            // Set world border.
                            WorldBorder worldBorder = world.getWorldBorder();
                            worldBorder.setCenter(new Location(world, 256, 100, 256));
                            worldBorder.setSize(512);

                            this.loaded = true;
                        });
                    }
                }
            }
            catch (SQLException | IOException exception) {
                exception.printStackTrace();
            }
        });
    }

    public boolean loaded() {
        return loaded;
    }

    public void save() {
        world.save();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                File regionFolder = new File(world.getWorldFolder(), "region");
                File regionFile = new File(regionFolder, "r.0.0.mca");
                InputStream inputStream = Files.newInputStream(regionFile.toPath());

                {
                    PreparedStatement statement = plugin.mySQL().getConnection().prepareStatement("REPLACE INTO housing_houses (houseUUID,ownerUUID,spawn) VALUES (?,?,?)");
                    statement.setString(1, houseUUID.toString());
                    statement.setString(2, ownerUUID.toString());
                    statement.setString(3, this.spawn);
                    statement.executeUpdate();
                }

                {
                    PreparedStatement statement = plugin.mySQL().getConnection().prepareStatement("REPLACE INTO housing_house_files (houseUUID,houseFile) VALUES (?,?)");
                    statement.setString(1, houseUUID.toString());
                    statement.setBlob(2, inputStream);
                    statement.executeUpdate();
                }

                saved = true;
            }
            catch (IOException | SQLException exception) {
                exception.printStackTrace();
            }

        });
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

    public Location spawnLocation() {
        String[] location = spawn.split(",");
        double x = Double.parseDouble(location[0]);
        double y = Double.parseDouble(location[1]);
        double z = Double.parseDouble(location[2]);
        float yaw = (float) Double.parseDouble(location[3]);
        float pitch = (float) Double.parseDouble(location[4]);

        return new Location(world, x, y, z, yaw, pitch);
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
            });
        });
    }

    public World world() {
        return world;
    }
}