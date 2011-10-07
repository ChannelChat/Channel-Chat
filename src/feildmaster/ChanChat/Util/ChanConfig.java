package feildmaster.ChanChat.Util;

import feildmaster.ChanChat.Chan.Channel;
import java.util.List;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

public class ChanConfig {
    private final Configuration config;

    public ChanConfig(Configuration c) {
        config = c;
        config.load();
        if(config.getAll().isEmpty() && config.getHeader() == null)
            createDefault();
        setHeader();
        loadChannels();
    }

    private void createDefault() {
        setHeader();
        config.setProperty("General.auto_join", true);
        config.setProperty("General.tag", "[G]");
        config.setProperty("General.listed", true);

        config.setProperty("Local.type", "Local");
        config.setProperty("Local.tag", "[L]");
        config.setProperty("Local.range", 1000);

        config.setProperty("World.type", "World");
        config.setProperty("World.tag", "[{World}]");

        config.save();
    }

    public void save() {
        for(String key : config.getKeys())
            config.removeProperty(key);

        for(Channel chan : ChatUtil.getCM().getChannels())
            if(chan.isSaved()) {
                String tag = chan.getName();
                config.setProperty(tag+".type", chan.getTypeName());
                config.setProperty(tag+".tag", chan.getTag());
                config.setProperty(tag+".owner", chan.getOwner());
                config.setProperty(tag+".password", chan.getPass());
                config.setProperty(tag+".listed", chan.isListed());
                config.setProperty(tag+".auto_join", chan.isAuto());
                config.setProperty(tag+".range", chan.getRange());
            }

        config.save();
    }

    public void reload() {
        config.load();
        List<String> keys = config.getKeys();
        // Erase non-existing channels!
        for(Channel chan : ChatUtil.getCM().getSavedChannels())
            if(!keys.contains(chan.getName()))
                ChatUtil.getCM().delChannel(chan.getName());

        for(String name : keys) {
            ConfigurationNode node = config.getNode(name);
            if(node == null)
                ChatUtil.getCM().addChannel(name, false);
            else
                ChatUtil.getCM().addChannel(name,
                    node.getString("type"),
                    node.getString("tag"),
                    node.getString("owner"),
                    node.getString("password"),
                    node.getInt("range", 0),
                    node.getBoolean("listed", false),
                    node.getBoolean("auto_join", false),
                    false);
        }
    }

    private void setHeader() {
        config.setHeader(
            "# ChannelName: #No spaces",
            "#    listed: #true/false",
            "#    range: # For local chats",
            "#    tag: #Channel Tag",
            "#    owner: #Owner Name",
            "#    auto_join: #true/false",
            "#    type: #Global/Local/World",
            "#    password: #Channel Password");
    }
    private void loadChannels() { // For now, I keep this function
        reload();
    }
}
