package net.jadedmc.housing.guis;

import net.jadedmc.housing.HousingPlugin;
import net.jadedmc.housing.houses.House;
import net.jadedmc.housing.houses.HouseMetaData;
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

public class VisitGUI extends CustomGUI {
    private final HousingPlugin plugin;

    public VisitGUI(HousingPlugin plugin, String uuid) {
        super(54, "Visit");
        this.plugin = plugin;

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
                        house.visit(p);
                    });
                    slot++;
                }
            }
        });
    }
}
