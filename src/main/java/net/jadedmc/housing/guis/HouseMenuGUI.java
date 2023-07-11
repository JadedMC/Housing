package net.jadedmc.housing.guis;

import net.jadedmc.housing.houses.House;
import net.jadedmc.housing.utils.gui.CustomGUI;
import net.jadedmc.housing.utils.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class HouseMenuGUI extends CustomGUI {
    private final House house;
    private final Player player;

    public HouseMenuGUI(House house, Player player) {
        super(54, "Housing Menu");
        addFiller(0,1,2,3,4,5,6,7,8,45,46,47,48,49,50,51,52,53);

        this.house = house;
        this.player = player;

        ItemStack membersItem = new ItemBuilder(Material.PLAYER_HEAD)
                .setDisplayName("&a&lMembers")
                .build();
        setItem(19, membersItem);

        ItemStack settingsItem = new ItemBuilder(Material.COMPARATOR)
                .setDisplayName("&a&lSettings")
                .build();
        setItem(28, settingsItem, (p,a) -> new HouseMenuSettingsGUI().open(p));

        ItemStack bannersItem = new ItemBuilder(Material.WHITE_BANNER)
                .setDisplayName("&a&lBanners")
                .build();
        setItem(22, bannersItem);

        ItemStack paintingsItem = new ItemBuilder(Material.PAINTING)
                .setDisplayName("&a&lPaintings")
                .build();
        setItem(23, paintingsItem);

        ItemStack headsItem = new ItemBuilder(Material.PLAYER_HEAD)
                .setDisplayName("&a&lHeads")
                .build();
        setItem(24, headsItem);

        ItemStack furnitureItem = new ItemBuilder(Material.OAK_STAIRS)
                .setDisplayName("&a&lFurniture")
                .build();
        setItem(25, furnitureItem);
    }

    private class HouseMenuSettingsGUI extends CustomGUI {
        public HouseMenuSettingsGUI() {
            super(54, "Housing Menu - Settings");
            addFiller(0,1,2,3,4,5,6,7,8,45,46,47,48,49,50,51,52,53);

            ItemStack iconItem = new ItemBuilder(Material.CRAFTING_TABLE)
                    .setDisplayName("&a&lIcon")
                    .build();
            setItem(19, iconItem);

            ItemStack houseNameItem = new ItemBuilder(Material.NAME_TAG)
                    .setDisplayName("&a&lHouse Name")
                    .build();
            setItem(28, houseNameItem);

            ItemStack visibilityItem = new ItemBuilder(Material.GLASS_BOTTLE)
                    .setDisplayName("&a&lVisibility")
                    .addLore("&7Visibility: &aPublic")
                    .build();
            setItem(29, visibilityItem);


            ItemStack weatherItem = new ItemBuilder(Material.WATER_BUCKET)
                    .setDisplayName("&a&lWeather")
                    .addLore("&7Weather: &aClear")
                    .build();
            setItem(22, weatherItem);

            ItemStack timeItem = new ItemBuilder(Material.CLOCK)
                    .setDisplayName("&a&lTime")
                    .build();
            setItem(23, timeItem);

            ItemStack gameRulesItem = new ItemBuilder(Material.COMPARATOR)
                    .setDisplayName("&a&lGame Rules")
                    .build();
            setItem(24, gameRulesItem);

            ItemStack defaultGameModeItem = new ItemBuilder(Material.GRASS_BLOCK)
                    .setDisplayName("&a&lDefault Game Mode")
                    .addLore("&7Mode: &aCreative")
                    .build();
            setItem(25, defaultGameModeItem);

            ItemStack pvpItem = new ItemBuilder(Material.IRON_SWORD)
                    .setDisplayName("&a&lPvP")
                    .addLore("&7PvP: &aEnabled")
                    .addFlag(ItemFlag.HIDE_ATTRIBUTES)
                    .build();
            setItem(34, pvpItem);
        }
    }
}