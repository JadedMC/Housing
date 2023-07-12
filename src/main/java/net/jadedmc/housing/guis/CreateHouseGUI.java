package net.jadedmc.housing.guis;

import net.jadedmc.housing.HousingPlugin;
import net.jadedmc.housing.houses.House;
import net.jadedmc.housing.houses.templates.Template;
import net.jadedmc.housing.utils.gui.CustomGUI;
import net.jadedmc.housing.utils.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;

public class CreateHouseGUI extends CustomGUI {
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
                house.visit(p);
            });
            slot++;
        }
    }
}
