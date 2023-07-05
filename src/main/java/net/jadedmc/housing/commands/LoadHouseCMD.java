package net.jadedmc.housing.commands;

import net.jadedmc.housing.HousingPlugin;
import net.jadedmc.housing.houses.House;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LoadHouseCMD extends AbstractCommand {
    private final HousingPlugin plugin;

    public LoadHouseCMD(HousingPlugin plugin) {
        super("loadhouse", "housing.admin", false);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            return;
        }

        Player player = (Player) sender;

        UUID houseUUID = UUID.fromString(args[0]);
        House house = plugin.houseManager().load(houseUUID);

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
    }
}
