package com.feildmaster.channelchat;

import com.feildmaster.channelchat.configuration.ModuleConfiguration;
import com.feildmaster.lib.configuration.PluginWrapper;
import org.bukkit.event.Listener;

public abstract class Module extends PluginWrapper {
    private ModuleConfiguration config;

    public void registerEvents(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    @Override
    public ModuleConfiguration getConfig() {
        if (config == null) {
            config = new ModuleConfiguration(this);
        }
        return config;
    }

    public void info(String message) {
        getServer().getLogger().info(format(message));
    }

    public String format(String message) {
        return String.format("[%1$s] %2$s", getDescription().getName(), message);
    }
}
