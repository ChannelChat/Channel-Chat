package com.feildmaster.channelchat.configuration;

import java.io.File;
import org.bukkit.plugin.Plugin;

public final class ChatConfiguration extends BetterYamlConfiguration {
    public ChatConfiguration(Plugin p, File file) {
        super(file, p);

        header("# Configuration",
               "# Creates automagically every start/reload");

        load();

        checkDefaults();
    }

    public boolean autoJoin() {
        return getBoolean("auto_set_on_join");
    }
}