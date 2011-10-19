package feildmaster.ChanChat;

import feildmaster.ChanChat.Chan.Channel;
import feildmaster.ChanChat.Chan.ChannelManager;
import feildmaster.ChanChat.Commands.*;
import feildmaster.ChanChat.Listeners.*;
import feildmaster.ChanChat.Util.*;
import java.io.File;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class Chat extends JavaPlugin {
    private static final ChannelManager cm = new ChannelManager();
    public static File module_folder;
    private ChatConfig config;
    private ChanConfig cConfig;

    public Chat() {
        module_folder = new File(getDataFolder(),"/modules/");
    }

    public void onDisable() {
        ChatUtil.save();

        ChatUtil.log.info(getDescription().getName()+" v"+getDescription().getVersion()+" disabled");
    }

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();

        // Events
        logInOut loginout = new logInOut();
        pm.registerEvent(Type.PLAYER_JOIN, loginout, Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_QUIT, loginout, Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_KICK, loginout, Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_CHAT, new ChatListener(), Priority.Highest, this);
        pm.registerEvent(Type.PLAYER_CHAT, new PasswordListener(), Priority.Lowest, this);

        // Setup configs. :D
        config = new ChatConfig(new File(getDataFolder(), "config.yml"));
        cConfig = new ChanConfig(new Configuration(new File(getDataFolder(), "channels.yml")));

        // Commands
        getCommand("channel-admin").setExecutor(new AdminCommand());
        getCommand("channel-chat").setExecutor(new ChatCommand());
        getCommand("channel-join").setExecutor(new JoinCommand());
        getCommand("channel-leave").setExecutor(new LeaveCommand());
        getCommand("channel-create").setExecutor(new CreateCommand());
        getCommand("channel-delete").setExecutor(new DeleteCommand());
        getCommand("channel-add").setExecutor(new AddCommand());
        getCommand("channel-set").setExecutor(new SetCommand());

        // AutoJoin Channels!
        for(Player player : getServer().getOnlinePlayers())
            for(Channel chan : cm.getAutoChannels())
                chan.addMember(player);

        ChatUtil.log.info(getDescription().getName()+" v"+getDescription().getVersion()+" enabled");
    }

    public ChannelManager getCM() {
        return cm;
    }

    public static ChannelManager getChannelManager() {
        return cm;
    }

    public ChatConfig getCC1() {
        return config;
    }
    public ChanConfig getCC2() {
        return cConfig;
    }
}

