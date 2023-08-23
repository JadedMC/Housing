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
import net.jadedmc.housing.houses.House;
import net.jadedmc.housing.utils.ChatUtils;
import net.jadedmc.housing.utils.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Represents
 */
public class SetNameCMD extends AbstractCommand {
    private final HousingPlugin plugin;

    /**
     * Creates the command.
     * @param plugin Instance of the plugin.
     */
    public SetNameCMD(HousingPlugin plugin) {
        super("setname", "", false);
        this.plugin = plugin;
    }

    /**
     * Executes the command.
     * @param sender The Command Sender.
     * @param args Arguments of the command.
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        // Only set a name if there is a name to set.
        if(args.length == 0) {
            ChatUtils.chat(player, "<red><bold>Usage</bold> <dark_gray>» <red>/setname [name]");
            return;
        }

        House house = plugin.houseManager().house(player.getWorld());

        // Make sure the player is actually in a house.
        if(house == null) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You can only do this in a house!");
            return;
        }

        // Only house members can change the house name.
        if(!house.isMember(player)) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>You do not own this house!");
            return;
        }

        String name = StringUtils.join(args, " ");

        // Makes sure the name will fit in the database.
        if(name.length() > 30) {
            ChatUtils.chat(player, "<red><bold>Error</bold> <dark_gray>» <red>That name is too long!");
            return;
        }

        // Update's the name.
        house.name(name);
        ChatUtils.chat(player, "<green><bold>Housing</bold> <dark_gray>» <green>House name set to " + name + "<green>!");
    }
}