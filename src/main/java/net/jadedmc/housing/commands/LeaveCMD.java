package net.jadedmc.housing.commands;

import net.jadedmc.housing.HousingPlugin;
import net.jadedmc.housing.houses.House;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCMD extends AbstractCommand {
    private final HousingPlugin plugin;

    public LeaveCMD(HousingPlugin plugin) {
        super("leave", "", false);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        House house = plugin.houseManager().house(player.getWorld());

        if(house == null) {
            return;
        }

        house.removePlayer(player);
    }
}
