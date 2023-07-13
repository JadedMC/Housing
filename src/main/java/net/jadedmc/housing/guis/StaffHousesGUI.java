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
import net.jadedmc.housing.houses.HouseMetaData;
import net.jadedmc.housing.utils.gui.CustomGUI;
import net.jadedmc.housing.utils.item.ItemBuilder;
import net.jadedmc.housing.utils.item.SkullBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class StaffHousesGUI extends CustomGUI {

    public StaffHousesGUI(HousingPlugin plugin) {
        super(54, "Housing Menu - Staff Houses");
        addFiller(0,1,2,3,4,5,6,7,8,45,46,47,48,49,50,51,52,53);

        if(plugin.houseManager().staffHouses().size() > 0) {
            int slot = 19;
            for(HouseMetaData houseMetaData : plugin.houseManager().staffHouses().values()) {
                ItemStack houseItem = new ItemBuilder(houseMetaData.icon())
                        .setDisplayName("&a" + houseMetaData.name())
                        .addLore("")
                        .addLore("&aClick to teleport")
                        .build();
                setItem(slot, houseItem, (p,a) -> {
                    UUID houseUUID = UUID.fromString(houseMetaData.uuid());
                    House house = plugin.houseManager().load(houseUUID);
                    house.visit(p);
                });
                slot++;
            }
        }
        else {
            ItemStack createHouseItem = new ItemBuilder(Material.GREEN_TERRACOTTA)
                    .setDisplayName("&a&lCreate a House")
                    .build();
            setItem(22, createHouseItem, (p,a) -> new CreateHouseGUI(plugin).open(p));
        }

        setItem(0, new SkullBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjg0ZjU5NzEzMWJiZTI1ZGMwNThhZjg4OGNiMjk4MzFmNzk1OTliYzY3Yzk1YzgwMjkyNWNlNGFmYmEzMzJmYyJ9fX0=").setDisplayName("&cBack").build(), (p, a) -> new HousingMenuGUI(plugin, p).open(p));
    }

}
