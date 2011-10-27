package com.feildmaster.chanchat.Util;

import java.io.File;
import org.bukkit.util.config.Configuration;

// TODO: owner-limit
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
        //getInt("ownership-limit", -1);
    }
}