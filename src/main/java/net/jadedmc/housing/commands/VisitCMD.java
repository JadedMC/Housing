package net.jadedmc.housing.commands;

import net.jadedmc.housing.HousingPlugin;
import net.jadedmc.housing.guis.VisitGUI;
import net.jadedmc.housing.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VisitCMD extends AbstractCommand {
    private final HousingPlugin plugin;

    public VisitCMD(HousingPlugin plugin) {
        super("visit", "", false);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            return;
        }

        // Runs MySQL async.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = plugin.mySQL().getConnection().prepareStatement("SELECT * FROM player_info WHERE username = ?");
                statement.setString(1, args[0]);
                ResultSet results = statement.executeQuery();

                if (results.next()) {
                    String uuid = results.getString(1);

                    Bukkit.getScheduler().runTask(plugin, () -> {
                        new VisitGUI(plugin, uuid).open(player);
                    });
                } else {
                    ChatUtils.chat(sender, "&cError &8» &cThat player has not played.");
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }
}
