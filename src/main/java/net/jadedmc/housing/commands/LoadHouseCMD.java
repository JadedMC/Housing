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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;


/**
 * Runs the /loadhouse command, which loads a house from its UUID.
 */
public class LoadHouseCMD extends AbstractCommand {
    private final HousingPlugin plugin;

    /**
     * Creates the command.
     * @param plugin Instance of the plugin.
     */
    public LoadHouseCMD(HousingPlugin plugin) {
        super("loadhouse", "housing.admin", false);
        this.plugin = plugin;
    }

    /**
     * Runs when the command is executed.
     * @param sender The Command Sender.
     * @param args Arguments of the command.
     */
    @Override
    public void execute(CommandSender sender, String[] args) {
        // Makes sure a UUID is listed.
        if(args.length == 0) {
            ChatUtils.chat(sender, "<red><bold>Usage</bold> <dark_gray>» <red>/loadhouse [uuid]");
            return;
        }

        Player player = (Player) sender;

        UUID houseUUID = UUID.fromString(args[0]);
        House house = plugin.houseManager().load(houseUUID);
        ChatUtils.chat(player, "<green>Loading world...");
        house.visit(player);
    }
}