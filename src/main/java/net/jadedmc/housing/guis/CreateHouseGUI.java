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
package net.jadedmc.housing.guis;

import net.jadedmc.housing.HousingPlugin;
import net.jadedmc.housing.houses.House;
import net.jadedmc.housing.houses.templates.Template;
import net.jadedmc.housing.utils.ChatUtils;
import net.jadedmc.housing.utils.gui.CustomGUI;
import net.jadedmc.housing.utils.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;

/**
 * This class runs the CreateHouse GUI, which allows a player to easily create a house from a list of templates.
 */
public class CreateHouseGUI extends CustomGUI {

    /**
     * Creates the GUI.
     * @param plugin Instance of the plugin.
     */
    public CreateHouseGUI(HousingPlugin plugin) {
        super(54, "Select A Template");
        addFiller(0,1,2,3,4,5,6,7,8,45,46,47,48,49,50,51,52,53);

        int slot = 19;
        for(Template template : plugin.templateManager().templates()) {
            ItemStack templateItem = new ItemBuilder(template.icon())
                    .setDisplayName("&a" + template.name())
                    .addLore("")
                    .addLore("&aClick to create!")
                    .build();
            setItem(slot, templateItem, (p,a) -> {
                House house = plugin.houseManager().createHouse(p, template.id());
                ChatUtils.chat(p, "<green>Loading world...");
                house.visit(p);
            });
            slot++;
        }
    }
}
