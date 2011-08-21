package feildmaster.ChanChat;

import feildmaster.ChanChat.Listeners.chatListener;
import feildmaster.ChanChat.Chan.ChannelManager;
import feildmaster.ChanChat.Commands.ChatCommand;
import feildmaster.ChanChat.Listeners.logInOut;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Chat extends JavaPlugin {
    private final ChannelManager cm = new ChannelManager();

    public void onDisable() {
    }

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        
        // Events
        logInOut loginout = new logInOut();
        chatListener chaterer = new chatListener();
        pm.registerEvent(Event.Type.PLAYER_JOIN, loginout, Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, loginout, Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_KICK, loginout, Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_CHAT, chaterer, Event.Priority.Monitor, this);
        
        // Commands
        getCommand("channel-chat").setExecutor(new ChatCommand());
    }
    
    public ChannelManager getCM() {
        return cm;
    }
}

