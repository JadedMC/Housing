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
package net.jadedmc.housing.houses;

import net.jadedmc.housing.HousingPlugin;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class HouseManager {
    private final HousingPlugin plugin;
    private final Collection<House> loadedHouses = new HashSet<>();
    private final Map<String, HouseMetaData> staffHouses = new HashMap<>();

    public HouseManager(HousingPlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            // Keep trying until a connection is made.
            while(plugin.mySQL().getConnection() == null) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                PreparedStatement statement = plugin.mySQL().getConnection().prepareStatement("SELECT * FROM housing_houses WHERE ownerUUID = ?");
                statement.setString(1, "JadedMC");
                ResultSet results = statement.executeQuery();

                while(results.next()) {
                    String houseUUID = results.getString("houseUUID");
                    staffHouses.put(houseUUID, new HouseMetaData(plugin, houseUUID));
                }
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public House createHouse(Player player, String templateID) {
        House house = new House(plugin, templateID, player);
        loadedHouses.add(house);

        return house;
    }

    public House house(UUID uuid) {
        for(House house : loadedHouses) {
            if(house.uuid().equals(uuid)) {
                return house;
            }
        }

        return null;
    }

    public House house(World world) {
        for(House house : loadedHouses) {
            if(house.world().equals(world)) {
                return house;
            }
        }

        return null;
    }

    public House load(UUID uuid) {
        House house = new House(plugin, uuid);
        loadedHouses.add(house);

        return house;
    }

    public Collection<House> houses() {
        return loadedHouses;
    }

    public Map<String, HouseMetaData> staffHouses() {
        return staffHouses;
    }

    public void unload(House house) {
        house.save();
        house.unload();
        loadedHouses.remove(house);
    }
}