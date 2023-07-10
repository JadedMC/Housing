package net.jadedmc.housing.commands;

import net.jadedmc.housing.HousingPlugin;
import net.jadedmc.housing.houses.House;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        String template = args[0];
        House house = plugin.houseManager().createHouse(player, template);
        house.visit(player);
    }
}
