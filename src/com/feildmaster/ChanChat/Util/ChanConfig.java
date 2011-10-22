package com.feildmaster.ChanChat.Util;

import com.feildmaster.ChanChat.Chan.*;
import com.feildmaster.ChanChat.Chan.Channel.Type;
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

        for(Channel chan : ChatUtil.getCM().getChannels()) {
            if(chan.isSaved()) {
                config.setProperty(chan.getName()+".type", chan.getType().name());
                config.setProperty(chan.getName()+".tag", chan.getTag());
                config.setProperty(chan.getName()+".owner", chan.getOwner());
                config.setProperty(chan.getName()+".password", chan.getPass());
                config.setProperty(chan.getName()+".listed", chan.isListed());
                config.setProperty(chan.getName()+".auto_join", chan.isAuto());

                // Channel Type Data
                if(chan.getType().equals(Channel.Type.Local)) {
                    config.setProperty(chan.getName()+".range", ((LocalChannel)chan).getRange());
                    config.setProperty(chan.getName()+".null_message", ((LocalChannel)chan).getNullMessage());
                }
            }
            chan.callSave();
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
            Channel chan;

            ConfigurationNode node = config.getNode(name);

            if(ChatUtil.getCM().channelExists(name)) { // TODO: Fix Channel type thing...
                chan = ChatUtil.getCM().getChannel(name);
                if(node != null) {
                    Channel.Type type = Channel.Type.betterValueOf(node.getString("type"));
                    if(!chan.getType().equals(type))
                        chan = ChatUtil.getCM().convertChannel(chan, type);
                }
            } else {
                if(node != null)
                    chan = ChatUtil.getCM().createChannel(name, Channel.Type.betterValueOf(node.getString("type")));
                else
                    chan = ChatUtil.getCM().createChannel(name, Channel.Type.Global);
            }

            if(node != null) {
                chan.setTag(node.getString("tag"));
                chan.setOwner(node.getString("owner"));
                chan.setPass(node.getString("password"));
                chan.setListed(node.getBoolean("listed", false));
                chan.setAuto(node.getBoolean("auto_join", false));

                // Type specific information
                if(chan.getType() == Type.Local) {
                    ((LocalChannel)chan).setNullMessage(node.getString("null_message"));
                    ((LocalChannel)chan).setRange(node.getInt("range", 1000));
                }
            }

            ChatUtil.getCM().addChannel(chan);
        }
    }

    private void setHeader() {
        config.setHeader(
            "#ChannelName: #No spaces in name",
            "#    auto_join: #true/false",
            "#    listed: #true/false",
            "#    owner: #Owner Name",
            "#    range: #For Local Channels",
            "#    tag: #Channel Tag",
            "#    type: #Global/Local/World",
            "#    password: #Channel Password",
            "#    shortcut: #Not yet. ;)");
    }
    private void loadChannels() { // For now, I keep this function
        reload();
    }
}
