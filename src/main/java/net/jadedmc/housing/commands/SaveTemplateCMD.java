package net.jadedmc.housing.commands;

import net.jadedmc.housing.HousingPlugin;
import net.jadedmc.housing.utils.ChatUtils;
import org.bukkit.command.CommandSender;

public class SaveTemplateCMD extends AbstractCommand {
    private final HousingPlugin plugin;

    public SaveTemplateCMD(HousingPlugin plugin) {
        super("savetemplate", "housing.admin", true);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(args.length < 2) {
            ChatUtils.chat(sender, "&c&lUsage &8» &c/savetemplate <templateID> <houseUUID>");
            return;
        }

        String templateID = args[0];
        String houseUUID = args[1];
        plugin.templateManager().createTemplate(templateID, houseUUID);
     }
}