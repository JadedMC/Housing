package net.jadedmc.housing.commands;

import net.jadedmc.housing.HousingPlugin;
import net.jadedmc.housing.utils.ChatUtils;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.*;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SaveTemplateCMD extends AbstractCommand {
    private final HousingPlugin plugin;

    public SaveTemplateCMD(HousingPlugin plugin) {
        super("savetemplate", "housing.admin", false);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(args.length == 0) {
            return;
        }

        if(args.length < 2) {
            return;
        }

        Player player = (Player) sender;
        String templateID = args[0];
        String houseUUID = args[1];
        plugin.templateManager().createTemplate(templateID, houseUUID);

        //plugin.templateManager().createTemplate(player, args[0]);


        /*
        Player player = (Player) sender;
        World world = player.getWorld();

        world.save();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {

            try {
                File regionFolder = new File(world.getWorldFolder(), "region");
                File regionFile = new File(regionFolder, "r.0.0.mca");
                InputStream inputStream = Files.newInputStream(regionFile.toPath());

                PreparedStatement statement = plugin.mySQL().getConnection().prepareStatement("REPLACE INTO housing_templates (templateID,templateFile,spawn) VALUES (?,?,?)");
                statement.setString(1, args[0]);
                statement.setBlob(2, inputStream);
                statement.setString(3, "256,100,256,0,0");
                statement.execute();
            }
            catch (SQLException | IOException exception) {
                exception.printStackTrace();
            }

            ChatUtils.chat(player, "&aTemplate saved!");
        });

         */
     }
}