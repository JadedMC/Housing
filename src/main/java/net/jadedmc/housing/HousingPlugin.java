package net.jadedmc.housing;

import net.jadedmc.housing.commands.AbstractCommand;
import net.jadedmc.housing.houses.House;
import net.jadedmc.housing.houses.HouseManager;
import net.jadedmc.housing.houses.templates.TemplateManager;
import net.jadedmc.housing.listeners.*;
import net.jadedmc.housing.player.HousingPlayerManager;
import net.jadedmc.housing.utils.ChatUtils;
import net.jadedmc.housing.utils.gui.GUIListeners;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class HousingPlugin extends JavaPlugin {
    private HouseManager houseManager;
    private MySQL mySQL;
    private SettingsManager settingsManager;
    private TemplateManager templateManager;
    private HousingPlayerManager housingPlayerManager;

    @Override
    public void onEnable() {
        // Plugin startup logic

        // Initialize an audiences instance for the plugin
        ChatUtils.setAdventure(BukkitAudiences.create(this));

        this.settingsManager = new SettingsManager(this);
        this.mySQL = new MySQL(this);

        this.houseManager = new HouseManager(this);
        this.templateManager = new TemplateManager(this);
        this.housingPlayerManager = new HousingPlayerManager(this);

        AbstractCommand.registerCommands(this);

        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        Bukkit.getPluginManager().registerEvents(new FoodLevelChangeListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerCommandPreprocessListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GUIListeners(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for(House house : houseManager.houses()) {
            house.save();
            house.unload();
        }
    }

    public HouseManager houseManager() {
        return houseManager;
    }

    public HousingPlayerManager housingPlayerManager() {
        return housingPlayerManager;
    }

    public MySQL mySQL() {
        return mySQL;
    }

    public SettingsManager settingsManager() {
        return settingsManager;
    }

    public TemplateManager templateManager() {
        return templateManager;
    }
}