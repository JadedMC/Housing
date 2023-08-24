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
import net.jadedmc.housing.utils.ChatUtils;
import net.jadedmc.housing.utils.gui.CustomGUI;
import net.jadedmc.housing.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Runs the Visit GUI, which displays all houses a player can visit from a specified player.
 */
public class VisitGUI extends CustomGUI {

    /**
     * Creates the GUI
     * @param plugin Instance of the plugin.
     * @param uuid Player's UUID to visit.
     */
    public VisitGUI(HousingPlugin plugin, String uuid) {
        super(54, "Visit");

        addFiller(0,1,2,3,4,5,6,7,8,45,46,47,48,49,50,51,52,53);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            List<HouseMetaData> houses = new ArrayList<>();

            try {
                PreparedStatement statement = plugin.mySQL().getConnection().prepareStatement("SELECT * FROM housing_houses WHERE ownerUUID = ?");
                statement.setString(1, uuid);
                ResultSet results = statement.executeQuery();

                while(results.next()) {
                    String houseUUID = results.getString("houseUUID");
                    houses.add(new HouseMetaData(plugin, houseUUID));
                }
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }

            if(houses.size() == 0) {
                setItem(22, new ItemBuilder(Material.RED_TERRACOTTA).setDisplayName("&cNo Houses").build());
            }
            else {
                int slot = 19;
                for(HouseMetaData houseMetaData : houses) {

                    while (!houseMetaData.loaded()) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    ItemStack houseItem = new ItemBuilder(houseMetaData.icon())
                            .setDisplayName("&a" + houseMetaData.name())
                            .addLore("")
                            .addLore("&aClick to visit!")
                            .build();
                    setItem(slot, houseItem, (p,a) -> {
                        House currentHouse = plugin.houseManager().house(p.getWorld());
                        if(currentHouse != null) {
                            currentHouse.removePlayer(p);
                        }

                        UUID houseUUID = UUID.fromString(houseMetaData.uuid());
                        House house = plugin.houseManager().load(houseUUID);
                        p.closeInventory();
                        ChatUtils.chat(p, "<green>Loading world...");
                        house.visit(p);
                    });
                    slot++;
                }
            }
        });
    }
}
