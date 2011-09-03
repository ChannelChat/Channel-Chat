package feildmaster.ChanChat.Util;

import org.bukkit.util.config.Configuration;

// TODO: Normal Config Options
public class ChatConfig {
    // Default config handler
    private final Configuration config;

    public ChatConfig(Configuration c) {
        config = c;
        config.load();
        config.setHeader("# Configuration",
                "# Options not enabled at the moment.");
        if(config.getAll().isEmpty())
            loadConfig();
        else
            load();
    }

    public void save() {
        config.save();
    }

    public void reload() {
//        do nothing for now. (No settings)
    }
    private void load() {
        reload();
    }
    private void loadConfig() {
        config.save();
        reload();
    }
}