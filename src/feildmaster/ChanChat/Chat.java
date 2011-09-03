package feildmaster.ChanChat;

import feildmaster.ChanChat.Listeners.chatListener;
import feildmaster.ChanChat.Chan.ChannelManager;
import feildmaster.ChanChat.Commands.ChatCommand;
import feildmaster.ChanChat.Commands.AddCommand;
import feildmaster.ChanChat.Commands.CreateCommand;
import feildmaster.ChanChat.Commands.DeleteCommand;
import feildmaster.ChanChat.Commands.JoinCommand;
import feildmaster.ChanChat.Commands.LeaveCommand;
import feildmaster.ChanChat.Commands.SetCommand;
import feildmaster.ChanChat.Listeners.logInOut;
import feildmaster.ChanChat.Util.ChanConfig;
import feildmaster.ChanChat.Util.ChatConfig;
import feildmaster.ChanChat.Util.ChatUtil;
import java.io.File;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class Chat extends JavaPlugin {
    private final ChannelManager cm = new ChannelManager();
    private ChatConfig config;
    private ChanConfig cConfig;

    public void onDisable() {
        ChatUtil.save();

        ChatUtil.log.info(getDescription().getName()+" v"+getDescription().getVersion()+" disabled");
    }

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();

        // Events
        logInOut loginout = new logInOut();
        chatListener chaterer = new chatListener();
        pm.registerEvent(Event.Type.PLAYER_JOIN, loginout, Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, loginout, Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_KICK, loginout, Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_CHAT, chaterer, Event.Priority.Highest, this);

        // Setup configs. :D
        config = new ChatConfig(getConfiguration());
        cConfig = new ChanConfig(new Configuration(new File(getDataFolder(), "channels.yml")));

        // Commands
        getCommand("channel-chat").setExecutor(new ChatCommand());
        getCommand("channel-join").setExecutor(new JoinCommand());
        getCommand("channel-leave").setExecutor(new LeaveCommand());
        getCommand("channel-create").setExecutor(new CreateCommand());
        getCommand("channel-delete").setExecutor(new DeleteCommand());
        getCommand("channel-add").setExecutor(new AddCommand());
        getCommand("channel-set").setExecutor(new SetCommand());

        ChatUtil.log.info(getDescription().getName()+" v"+getDescription().getVersion()+" enabled");
    }

    public ChannelManager getCM() {
        return cm;
    }

    public ChatConfig getCC1() {
        return config;
    }
    public ChanConfig getCC2() {
        return cConfig;
    }
}

