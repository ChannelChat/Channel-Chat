package com.feildmaster.channelchat.configuration;

import java.io.File;
import org.bukkit.util.config.Configuration;

public class ChatConfig extends Configuration {
    public ChatConfig(File file) {
        super(file);
        load();
        setHeader("# Configuration",
                  "# Creates automagically every start/reload");
        load_config();
        save();
    }

    public void reload() {
        load();
        load_config();
    }
    private void load_config() {
        //getBoolean("auto-set-on-join", false);
    }
}