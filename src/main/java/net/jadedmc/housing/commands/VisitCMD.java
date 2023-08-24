/*
 * This file is part of JadedChat, licensed under the MIT License.
 *
 *  Copyright (c) JadedMC
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
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

/**
 * This class runs the /visit command, which allows a player to visit another player's house.
 */
public class VisitCMD extends AbstractCommand {
    private final HousingPlugin plugin;

    /**
     * Creates the command.
     * @param plugin Instance of the plugin.
     */
    public VisitCMD(HousingPlugin plugin) {
        super("visit", "", false);
        this.plugin = plugin;
    }


    /**
     * Runs when the command is executed.
     * @param sender The Command Sender.
     * @param args Arguments of the command.
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        // Makes sure a player to visit is entered.
        if(args.length == 0) {
            ChatUtils.chat(player, "<red><bold>Usage</bold> <dark_gray>» <red>/visit [player]");
            return;
        }

        // Makes sure the player entered is not the one running the command.
        if(args[0].equalsIgnoreCase(player.getName())) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You cannot visit yourself!");
            return;
        }

        // Run MySQL tasks async.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = plugin.mySQL().getConnection().prepareStatement("SELECT * FROM player_info WHERE username = ?");
                statement.setString(1, args[0]);
                ResultSet results = statement.executeQuery();

                if(results.next()) {
                    String uuid = results.getString(1);
                    Bukkit.getScheduler().runTask(plugin, () -> new VisitGUI(plugin, uuid).open(player));
                }
                else {
                    ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>That player has not played!");
                }
            }
            catch(SQLException exception) {
                exception.printStackTrace();
            }
        });
    }
}