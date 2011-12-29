package com.feildmaster.channelchat.configuration;

import java.io.File;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class BetterYamlConfiguration extends YamlConfiguration {
    private final Plugin plugin;
    private final File file;

    public BetterYamlConfiguration(File file, Plugin plugin) {
        this.file = file;
        this.plugin = plugin;

        options().indent(4); // Override to always indent 4 spaces

        load();
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

    public void reload() {
        load();
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

    /**
     * Simple function for if the Configuration file exists
     *
     * @return True if configuration exists on disk
     */
    public final boolean exists() {
        return file.exists();
    }

    /**
     * Loads a file from the plugin jar and sets as default
     *
     * @param filename The filename to open
     */
    public final void loadDefaults(String filename) {
        setDefaults(YamlConfiguration.loadConfiguration(plugin.getResource(filename)));
    }

    /**
     * Saves current configuration (plus defaults) to disk.
     *
     * If defaults and configuration are empty, saves blank file.
     *
     * @return True if saved successfully
     */
    public final boolean saveDefaults() {
        try {
            options().copyDefaults(true);
            save();
            options().copyDefaults(false);
            return true;
        } catch (Exception ex) {
            options().copyDefaults(false);
            return false;
        }
    }

    public BetterYamlConfiguration header(String... header) {
        StringBuilder string = new StringBuilder();

        if(header[0] == null) return this; // Owned
        else string.append(header[0]);


        for(int x=1; x<header.length; x++) {
            String s = header[x];
            if(s == null) continue;
            else string.append("\n").append(s);
        }

        options().header(string.toString());

        return this;
    }

    /**
     * Clears current configuration defaults
     */
    public final void clearDefaults() {
        setDefaults(new MemoryConfiguration());
    }

    public void checkDefaults() {
        if(getValues(true).size() < getDefaults().getValues(true).size()) {
            saveDefaults();
        }
    }
}
