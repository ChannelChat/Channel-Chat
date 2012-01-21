package com.feildmaster.channelchat;

import com.feildmaster.channelchat.configuration.ModuleConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Module extends JavaPlugin {
    private ModuleConfiguration config;

    public void registerEvents(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public ModuleConfiguration getConfig() {
        if(config == null) {
            reloadConfig();
        }

        return config;
    }

    public void reloadConfig() {
        if(config == null) {
            config = new ModuleConfiguration(this);
        }

        config.reload();
    }

    public void saveConfig() {
        config.save();
    }

    public void info(String message) {
        getServer().getLogger().info(format(message));
    }

    public String format(String message) {
        return String.format("[%1$s] %2$s", getDescription().getName(), message);
    }
}
