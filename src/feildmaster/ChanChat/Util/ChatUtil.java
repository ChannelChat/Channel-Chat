package feildmaster.ChanChat.Util;

import com.massivecraft.factions.Factions;
import feildmaster.ChanChat.Chan.ChannelManager;
import feildmaster.ChanChat.Chat;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.config.Configuration;

public class ChatUtil {
    private static final Server server = Bukkit.getServer();
    public static final Logger log = getServer().getLogger();
    private static Chat chatPlugin;
    private static Factions factionPlugin;
    private static PluginManager pm;

    public static Server getServer() {
        return server;
    }
    public static PluginManager getPluginManager() {
        if(pm == null)pm = server.getPluginManager();
        return pm;
    }
    public static Chat getChatPlugin() {
        if(chatPlugin == null) {
            chatPlugin = (Chat)getPluginManager().getPlugin("ChannelChat");
            factionPlugin = (Factions)getPluginManager().getPlugin("Factions");
        }
        return chatPlugin;
    }
    public static Factions getFactionPlugin() {
        return factionPlugin;
    }
    public static ChannelManager getCM() {
        return getChatPlugin().getCM();
    }

    public static Configuration getDefaultConfig() {
        return getChatPlugin().getConfiguration();
    }
    public static Player getPlayer(String name) {
        return getServer().getPlayer(name);
    }
    public static void reload() {
        getChatPlugin().getCC1().reload();
        getChatPlugin().getCC2().reload();
    }
    public static void save() {
        getChatPlugin().getCC1().save();
        getChatPlugin().getCC2().save();
    }
}
