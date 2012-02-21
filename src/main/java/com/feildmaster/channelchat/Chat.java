package com.feildmaster.channelchat;

import java.io.File;
import com.feildmaster.channelchat.command.core.*;
import com.feildmaster.channelchat.channel.*;
//import com.feildmaster.channelchat.command.*;
import com.feildmaster.channelchat.listener.*;
import com.feildmaster.channelchat.configuration.*;
import static com.feildmaster.channelchat.event.ChannelEventFactory.*;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

// TODO: Default "set" channel
// TODO: /cc help or something
public class Chat extends JavaPlugin {

    private static Chat plugin;
    private ChatConfiguration config;
    private ChannelConfiguration cConfig;
    private Listener[] listeners = {new LoginListener(), new ChatListener()};

    public void onDisable() {
        saveConfig();
    }

    public void onEnable() {
        plugin = this;

        // Events
        for (Listener l : listeners) {
            getServer().getPluginManager().registerEvents(l, this);
        }

        if(getConfig().needsUpdate()) {
            getConfig().saveDefaults();
        }

        cConfig = new ChannelConfiguration(new Configuration(new File(getDataFolder(), "channels.yml")));

        // Commands
        getCommand("channel-admin").setExecutor(new Admin());
        getCommand("channel-chat").setExecutor(new CChat());
        getCommand("channel-join").setExecutor(new Join());
        getCommand("channel-leave").setExecutor(new Leave());
        getCommand("channel-create").setExecutor(new Create());
        getCommand("channel-delete").setExecutor(new Delete());
        getCommand("channel-add").setExecutor(new Add());
        getCommand("channel-set").setExecutor(new Set());

        // AutoJoin Channels!
        for (Player player : getServer().getOnlinePlayers()) {
            ChannelManager.getManager().joinAutoChannels(player);
            ChannelManager.getManager().checkActive(player); // Fixes null "active"
        }
    }

    public ChatConfiguration getConfig() {
        if (config == null) {
            config = new ChatConfiguration(this);
        }
        return config;
    }

    public void saveConfig() {
        getConfig().save();
        cConfig.save();
        callSaveEvent();
    }

    public void reloadConfig() {
        getConfig().load();
        cConfig.reload();
        callReloadEvent();
    }

    public static Chat plugin() {
        return plugin;
    }

    public static String error(String msg) {
        return format(ChatColor.RED, msg);
    }

    public static String info(String msg) {
        return format(ChatColor.YELLOW, msg);
    }

    public static String format(ChatColor color, String msg) {
        return "[" + plugin.getDescription().getName() + "] " + (color == null ? "" : color) + msg;
    }

    /**
     * Formats message to include plugin name tag
     *
     * @param recipiant Player/Object recieving the message
     * @param string The message to send
     */
    public void sendMessage(CommandSender recipiant, String string) {
        recipiant.sendMessage(format(null, string));
    }
}
