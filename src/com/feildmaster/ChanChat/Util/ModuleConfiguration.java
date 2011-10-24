package com.feildmaster.ChanChat.Util;

import com.feildmaster.ChanChat.Chat;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Configuration class for simple configuration setting for ChannelChat Plugins
 */
public class ModuleConfiguration extends YamlConfiguration {
    private final JavaPlugin plugin;
    private final File file;
    private InputStream stream;

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
        plugin = p;
        file = new File(Chat.getModuleFolder(), configName);

        load();
        loadDefaults(configName);
    }

    /**
     * Loads the configuration from disk
     *
     * @return True if loaded successfully
     */
    public final boolean load() {
        try {
            load(file);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Saves the configuration to disk
     *
     * @return True if saved successfully
     */
    public final boolean save() {
        try {
            save(file);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public final void loadDefaults(String filename) {
        stream = plugin.getResource(filename);
        if(stream != null)
            setDefaults(YamlConfiguration.loadConfiguration(stream));
    }

    /**
     * Call this function when disabling your plugin to prevent errors!!!
     */
    public final void closeDefaults() {
        try {
            stream.close();
        } catch (IOException ex) {}
        stream = null;
    }
}
