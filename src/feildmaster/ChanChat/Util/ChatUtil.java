package feildmaster.ChanChat.Util;

import feildmaster.ChanChat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.config.Configuration;

public class ChatUtil {
    private static final Server server = Bukkit.getServer();
    private static Chat chatPlugin;
    private static PluginManager pm;
    
    public static Server getServer() {
        return server;
    }
    public static PluginManager getPluginManager() {
        if(pm == null)pm = server.getPluginManager();
        return pm;
    }
    public static Chat getChatPlugin() {
        if(chatPlugin == null) chatPlugin = (Chat)getPluginManager().getPlugin("ChannelChat");
        return chatPlugin;
    }
    public static Configuration getDefaultConfig() {
        return getChatPlugin().getConfiguration();
    }
}
