package com.feildmaster.channelchat.configuration;

import com.feildmaster.channelchat.Chat;
import com.feildmaster.channelchat.channel.*;
import com.feildmaster.channelchat.channel.Channel.Type;
import static com.feildmaster.channelchat.channel.ChannelManager.getManager;
import com.feildmaster.lib.configuration.NullEnhancedConfiguration;
import org.bukkit.configuration.ConfigurationSection;

public class ChannelConfiguration extends NullEnhancedConfiguration {
    public ChannelConfiguration(Chat plugin) {
        super("channels.yml", plugin);

        if (loadDefaults()) {
            if (checkDefaults() || options().header() == null) {
                saveDefaults();
            }
        }

        reload();
    }

    @Override
    public boolean save() {
        for (Channel chan : getManager().getChannels()) {
            if (chan.isSaved()) {
                createSection(chan.getName(), chan.serialize());
            }
        }
        return super.save();
    }

    public final void reload() {
        // !!! This function is actually broken
        // Erase non-existing channels!
//        for (Channel chan : getManager().getSavedChannels()) {
//            if (!keys.contains(chan.getName())) {
//                getManager().deleteChannel(chan.getName());
//            }
//        }

        for (String name : getKeys(false)) {
            Channel chan;
            ConfigurationSection node = getConfigurationSection(name);

            if (getManager().channelExists(name)) {
                chan = getManager().getChannel(name);
                if (node != null) {
                    Channel.Type type = Channel.Type.betterValueOf(node.getString("type"));
                    if (!chan.getType().equals(type)) {
                        chan = getManager().convertChannel(chan, type);
                    }
                }
            } else {
                chan = getManager().createChannel(name, node == null ? Channel.Type.Global : Channel.Type.betterValueOf(node.getString("type")));
            }

            if (node != null) {
                chan.setTag(node.getString("tag"));
                chan.setOwner(node.getString("owner"));
                chan.setPass(node.getString("password"));
                chan.setListed(node.getBoolean("listed"));
                chan.setAuto(node.getBoolean("auto_join"));
                chan.setAlias(node.getString("alias"));

                // Type specific information
                if (chan.getType() == Type.Local) {
                    ((LocalChannel) chan).setNullMessage(node.getString("null_message"));
                    ((LocalChannel) chan).setRange(node.getInt("range", 1000));
                }
            }

            getManager().addChannel(chan);
        }
    }
}
