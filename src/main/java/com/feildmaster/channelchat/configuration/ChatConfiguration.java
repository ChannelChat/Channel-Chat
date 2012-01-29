package com.feildmaster.channelchat.configuration;

import com.feildmaster.lib.configuration.EnhancedConfiguration;
import org.bukkit.plugin.Plugin;

public final class ChatConfiguration extends EnhancedConfiguration {
    public ChatConfiguration(Plugin p) {
        super(p);
        if(!loadDefaults()) {
            System.out.print(getLastException());
        }
    }

    public boolean autoSet() {
        return getBoolean("auto_set_on_join");
    }

    public boolean persistRelog() {
        return getBoolean("persist_relogs");
    }

    public boolean allowCreateChannels() {
        return getBoolean("allow-create-channels");
    }

    public boolean needsUpdate() {
        return !fileExists() || !checkDefaults();
    }
}