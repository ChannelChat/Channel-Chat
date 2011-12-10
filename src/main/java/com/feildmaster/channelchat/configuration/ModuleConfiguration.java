package com.feildmaster.channelchat.configuration;

import java.io.File;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Configuration class for simple configuration setting for ChannelChat Plugins
 */
public class ModuleConfiguration extends BetterYamlConfiguration {
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
        super(new File(new File("plugins/ChannelChat/modules"), configName), p);

        loadDefaults(configName);
    }

//
//    public Map<String, Object> getMap(String path) {
//        Map<String, Object> map = new HashMap<String, Object>();
//
//        Object o = get(path);
//        if(o instanceof ConfigurationSection) {
//            return ((ConfigurationSection)o).getValues(false);
//        }
//
//        return map;
//    }
}
