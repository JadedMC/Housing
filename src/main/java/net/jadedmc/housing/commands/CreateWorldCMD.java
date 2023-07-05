package net.jadedmc.housing.commands;

import net.jadedmc.housing.houses.generator.EmptyChunkGenerator;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateWorldCMD extends AbstractCommand {

    public CreateWorldCMD() {
        super("createworld", "housing.admin", false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if(args.length == 0) {
            return;
        }

        String worldName = args[0];

        WorldCreator worldCreator = new WorldCreator(worldName);
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
     }
}