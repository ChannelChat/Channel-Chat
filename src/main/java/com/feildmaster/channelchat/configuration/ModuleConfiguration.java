package com.feildmaster.channelchat.configuration;

import com.feildmaster.lib.configuration.EnhancedConfiguration;
import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Configuration class for simple configuration setting for ChannelChat Plugins
 */
public class ModuleConfiguration extends EnhancedConfiguration {
    private static File directory;

    /**
     * Creates a new ModuleConfiguration for ChannelChat modules.
     * Uses plugin name for file name
     *
     * @param p Plugin creating the configuration
     */
    public ModuleConfiguration(JavaPlugin p) {
        this(p, p.getDescription().getName()+".yml");
    }

    /**
     * Creates a new ModuleConfiguration for ChannelChat modules
     *
     * @param p Plugin creating the configuration
     * @param configName The name of the default YamlConfiguration file. (Ex: "PluginConfig.yml")
     */
    public ModuleConfiguration(JavaPlugin p, String configName) {
        super(new File(directory, configName), p);

        loadDefaults(configName);
    }

    static {
        directory = new File(Bukkit.getServer().getPluginManager().getPlugin("ChannelChat").getDataFolder(), "modules");
    }
}
