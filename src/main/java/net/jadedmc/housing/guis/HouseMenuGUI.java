package net.jadedmc.housing.guis;

import net.jadedmc.housing.houses.House;
import net.jadedmc.housing.utils.gui.CustomGUI;
import net.jadedmc.housing.utils.item.ItemBuilder;
import net.jadedmc.housing.utils.item.SkullBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class HouseMenuGUI extends CustomGUI {

    public HouseMenuGUI(Player player) {
        super(54, "Housing Menu");
        addFiller(0,1,2,3,4,5,6,7,8,45,46,47,48,49,50,51,52,53);


    }

    public HouseMenuGUI(House house, Player player) {
        super(54, "Housing Menu");
        addFiller(0,1,2,3,4,5,6,7,8,45,46,47,48,49,50,51,52,53);

        ItemStack membersItem = new ItemBuilder(Material.PLAYER_HEAD)
                .setDisplayName("&a&lMembers")
                .build();
        setItem(19, membersItem);

        ItemStack settingsItem = new ItemBuilder(Material.COMPARATOR)
                .setDisplayName("&a&lSettings")
                .build();
        setItem(28, settingsItem, (p,a) -> new HouseMenuSettingsGUI(house, player).open(p));

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

        ItemStack craftingTableItem = new ItemBuilder(Material.CRAFTING_TABLE)
                .setDisplayName("&a&lCrafting Table")
                .build();
        setItem(38, craftingTableItem, (p,a) -> p.openWorkbench(p.getLocation(), true));

        ItemStack anvilItem = new ItemBuilder(Material.ANVIL)
                .setDisplayName("&a&lAnvil")
                .build();
        setItem(39, anvilItem, (p,a) -> p.openAnvil(p.getLocation(), true));

        ItemStack smithingTableItem = new ItemBuilder(Material.SMITHING_TABLE)
                .setDisplayName("&a&lSmithing Table")
                .build();
        setItem(40, smithingTableItem, (p,a) -> p.openSmithingTable(p.getLocation(), true));

        ItemStack loomItem = new ItemBuilder(Material.LOOM)
                .setDisplayName("&a&lLoom")
                .build();
        setItem(41, loomItem, (p,a) -> p.openLoom(p.getLocation(), true));

        ItemStack cartographyTableItem = new ItemBuilder(Material.CARTOGRAPHY_TABLE)
                .setDisplayName("&a&lCartography Table")
                .build();
        setItem(42, cartographyTableItem, (p,a) -> p.openCartographyTable(p.getLocation(), true));
    }

    private static class HouseMenuSettingsGUI extends CustomGUI {
        public HouseMenuSettingsGUI(House house, Player player) {
            super(54, "Housing Menu - Settings");
            addFiller(1,2,3,4,5,6,7,8,45,46,47,48,49,50,51,52,53);

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

            setItem(0, new SkullBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjg0ZjU5NzEzMWJiZTI1ZGMwNThhZjg4OGNiMjk4MzFmNzk1OTliYzY3Yzk1YzgwMjkyNWNlNGFmYmEzMzJmYyJ9fX0=").setDisplayName("&cBack").build(), (p, a) -> new HouseMenuGUI(house, player).open(p));
        }
    }
}