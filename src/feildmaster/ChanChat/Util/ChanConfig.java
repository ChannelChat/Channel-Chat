package feildmaster.ChanChat.Util;

import org.bukkit.util.config.Configuration;

public class ChanConfig {
    private Configuration config;
    
    public ChanConfig(Configuration c) {
        config = c;
        if(config.getAll().isEmpty()) {
            createDefault();
        }
    }
    
    private void createDefault() {
        
    }
}
