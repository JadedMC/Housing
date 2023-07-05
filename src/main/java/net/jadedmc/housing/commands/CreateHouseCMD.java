package net.jadedmc.housing.commands;

import net.jadedmc.housing.HousingPlugin;
import net.jadedmc.housing.houses.House;
import net.jadedmc.housing.houses.generator.EmptyChunkGenerator;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CreateHouseCMD extends AbstractCommand {
    private final HousingPlugin plugin;

    public CreateHouseCMD(HousingPlugin plugin) {
        super("createhouse", "", false);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            return;
        }

        Player player = (Player) sender;
        String houseUUID = UUID.randomUUID().toString();

        String template = args[0];
        House house = plugin.houseManager().createHouse(player, template);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            while(!house.loaded()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            plugin.getServer().getScheduler().runTask(plugin, () -> player.teleport(house.world().getSpawnLocation()));
        });

        /*
        // Generates the world files.
        WorldCreator tempWorldCreator = new WorldCreator(houseUUID);
        tempWorldCreator.generator(new EmptyChunkGenerator());
        World tempWorld = tempWorldCreator.createWorld();
        assert tempWorld != null;
        Bukkit.unloadWorld(tempWorld, false);

        // Get the map from MySQL.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {

            // Keep trying until a connection is made.
            while(plugin.mySQL().getConnection() == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                PreparedStatement statement = plugin.mySQL().getConnection().prepareStatement("SELECT * FROM housing_templates WHERE templateID = ? LIMIT 1");
                statement.setString(1, template);
                ResultSet resultSet = statement.executeQuery();

                // If it finds the map, create the world.
                if(resultSet.next()) {
                    // Get the region file from MySQL.
                    Blob blob = resultSet.getBlob("templateFile");
                    InputStream inputStream = blob.getBinaryStream();

                    // Save the file to the server.
                    File regionFolder = new File(tempWorld.getWorldFolder(), "region");
                    File regionFile = new File(regionFolder, "r.0.0.mca");
                    Files.copy(inputStream, regionFile.toPath());

                    // Create and load the world.
                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        // Create the world.
                        WorldCreator worldCreator = new WorldCreator(houseUUID);
                        worldCreator.generator(new EmptyChunkGenerator());
                        World world = worldCreator.createWorld();

                        // Set gamerules.
                        assert world != null;
                        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
                        world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);

                        // Set time and weather.
                        world.setStorm(false);
                        world.setTime(6000);

                        // Set world border.
                        WorldBorder worldBorder = world.getWorldBorder();
                        worldBorder.setCenter(new Location(world, 256, 100, 256));
                        worldBorder.setSize(512);

                        player.teleport(world.getSpawnLocation());
                    });
                }

            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }

        });

         */
    }
}
