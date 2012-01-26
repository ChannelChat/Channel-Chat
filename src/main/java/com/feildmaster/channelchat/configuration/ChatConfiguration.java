package com.feildmaster.channelchat.configuration;

import com.feildmaster.lib.configuration.EnhancedConfiguration;
import java.io.File;
import org.bukkit.plugin.Plugin;

public final class ChatConfiguration extends EnhancedConfiguration {
    public ChatConfiguration(Plugin p, File file) {
        super(file, p);

        loadDefaults(file.getName());
        checkDefaults();

        load();
    }

    public boolean autoSet() {
        return getBoolean("auto_set_on_join");
    }

    public boolean persistRelog() {
        return getBoolean("persist_relogs");
    }
}