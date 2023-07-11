package net.jadedmc.housing.commands;

import net.jadedmc.housing.HousingPlugin;
import net.jadedmc.housing.guis.HouseMenuGUI;
import net.jadedmc.housing.houses.House;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MenuCMD extends AbstractCommand {
    private final HousingPlugin plugin;

    public MenuCMD(HousingPlugin plugin) {
        super("menu", "", false);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        House house = plugin.houseManager().house(player.getWorld());

        if(house == null) {
            return;
        }

        if(!house.isMember(player)) {
            return;
        }

        new HouseMenuGUI(house, player).open(player);
    }
}
