package feildmaster.ChanChat.Util;

import feildmaster.ChanChat.Chan.Channel;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

public class ChanConfig {
    // Channels.yml
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
        config.setProperty("Default.auto_join", true);
        config.save();
    }

    public void save() {
        config.getAll().clear();
        for(Channel chan : ChatUtil.getCM().getChannels())
            if(chan.isSaved()) {
                String tag = chan.getName();
                config.setProperty(tag, null);
                if(chan.getOwner() != null)
                    config.setProperty(tag+".owner", chan.getOwner());
                if(chan.getPass() != null)
                    config.setProperty(tag+".password", chan.getPass());
                if(chan.isAuto())
                    config.setProperty(tag+".auto_join", true);
            }

        config.save();
    }

    public void reload() {
        config.load();
        for(String name : config.getKeys()) {
            ConfigurationNode node = config.getNode(name);
            if(node == null)
                ChatUtil.getCM().addChannel(name, null, null, false, false);
            else
                ChatUtil.getCM().addChannel(name,
                    node.getString("owner", null),
                    node.getString("password", null),
                    node.getBoolean("auto_join", false),
                    false);
        }
    }

    private void setHeader() {
        config.setHeader(
            "# ChannelName: #No spaces",
            "#    owner: #Owner Name",
            "#    password: #Channel Password",
            "#    auto_join: #true/false");
    }
    private void loadChannels() { // For now, I keep this function
        reload();
    }
}
