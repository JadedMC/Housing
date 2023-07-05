package net.jadedmc.housing.houses;

import net.jadedmc.housing.HousingPlugin;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class HouseManager {
    private final HousingPlugin plugin;
    private final Collection<House> loadedHouses = new HashSet<>();

    public HouseManager(HousingPlugin plugin) {
        this.plugin = plugin;
    }

    public House createHouse(Player player, String templateID) {
        House house = new House(plugin, templateID, player);
        loadedHouses.add(house);

        return house;
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

    public void unload(House house) {
        house.save();
        house.unload();
        loadedHouses.remove(house);
    }
}