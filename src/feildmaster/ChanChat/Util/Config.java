package feildmaster.ChanChat.Util;

import java.io.File;
import java.util.Map;
import org.bukkit.util.config.Configuration;

// This config is a bit of a joke. :D

public class Config {
    private final Configuration config;
    
    /* Setup Configuration Instances
     * Either:
     *      - new Config("name"); // Not recommended, saves in root server folder
     *      - new Config("folder", "name"); // reads "file" in "folder"
     *      - new Config(file); // Passes the config file directly
     */
    public Config(String name) {
        config = new Configuration(new File(name));
        config.load();
    }
    public Config(String folder, String name) {
        config = new Configuration(new File(folder, name));
        config.load();
    }
    public Config(File file) {
        config = new Configuration(file);
        config.load();
    }
    public Config (Configuration config) {
        this.config = config;
        this.config.load();
    }
    
    // Function for people that like editing directly
    public Configuration getConfig() {
        return config;
    }
    
    // Header Functions (Adds in Comment Hashes)
    public void setHeader(String header) {
        config.setHeader((!header.startsWith("#")?"# ":"")+header);
    }
    public void setHeader(String... headerlines) {
        StringBuilder header = new StringBuilder();
        
        for(String line : headerlines)
            header.append(!line.startsWith("#")?"# ":"").append(line).append(header.length()>0?"\r\n":"");
        
        setHeader(header.toString());
    }
    
    // Reload/Save functions
    public void reload() {
        config.load();
        Map<String, Object> map = config.getAll();
        for(String node : map.keySet()) {
            config.setProperty(node, map.get(node));
        }
        config.save();
    }
    public void save() {
        config.save();
    }
}
