package net.jadedmc.housing.commands;

import net.jadedmc.housing.HousingPlugin;
import net.jadedmc.housing.houses.House;
import net.jadedmc.housing.utils.ChatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TransferCMD extends AbstractCommand {
    private final HousingPlugin plugin;

    public TransferCMD(HousingPlugin plugin) {
        super("transfer", "housing.admin", false);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        House house = plugin.houseManager().house(player.getWorld());

        if(house == null) {
            return;
        }

        if(args.length == 0) {
            return;
        }

        house.transfer(args[0]);
        ChatUtils.chat(player, "<green>House has been transferred to <white>" + args[0] + "<green>!");
    }
}
