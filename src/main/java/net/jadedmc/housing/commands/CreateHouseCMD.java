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
import net.jadedmc.housing.guis.CreateHouseGUI;
import net.jadedmc.housing.houses.House;
import net.jadedmc.housing.utils.ChatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This class runs the /createhouse command, which allows a player to create a house.
 */
public class CreateHouseCMD extends AbstractCommand {
    private final HousingPlugin plugin;

    /**
     * Creates the command.
     * @param plugin Instance of the plugin.
     */
    public CreateHouseCMD(HousingPlugin plugin) {
        super("createhouse", "", false);
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

        // Opens the gui if no arguments were used.
        if(args.length == 0) {
            new CreateHouseGUI(plugin).open(player);
            return;
        }

        // Creates a house using the given template.
        String template = args[0];
        House house = plugin.houseManager().createHouse(player, template);
        ChatUtils.chat(player, "<green>Loading world...");
        house.visit(player);
    }
}