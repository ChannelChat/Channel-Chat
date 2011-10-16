package feildmaster.ChanChat.Listeners;

import feildmaster.ChanChat.Chan.Channel;
import feildmaster.ChanChat.Chan.ChannelManager;
import feildmaster.ChanChat.Chan.LocalChannel;
import feildmaster.ChanChat.Chan.WorldChannel;
import feildmaster.ChanChat.Events.ChannelPlayerChatEvent;
import feildmaster.ChanChat.Util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

/*
 * ChatListener
 * <p>
 * Handles player chat
 */
public class ChatListener extends PlayerListener {
    private final ChannelManager cm = ChatUtil.getCM();

    public void onPlayerChat(PlayerChatEvent event) {
        if(event.isCancelled()) return;
        Player player = event.getPlayer();

        if(ChatUtil.getFactionPlugin() != null && ChatUtil.getFactionPlugin().isPlayerFactionChatting(player))
            return;

        if(cm.getJoinedChannels(player).isEmpty()) {
            player.sendMessage(ChatColor.YELLOW+"You are not in any channels.");
            event.setCancelled(true);
        } else {
            cm.checkActive(player);
            Channel chan = event instanceof ChannelPlayerChatEvent ? ((ChannelPlayerChatEvent)event).getChannel() : cm.getActiveChan(player);

            if(chan != null) {
                switch(chan.getType()) {
                    case Faction:
                        chan.toFaction().handleEvent(event);
                        break;
                    case World:
                        chan.toWorld().handleEvent(event);
                        break;
                    case Local:
                        chan.toLocal().handleEvent(event);
                        break;
                    default:
                        chan.handleEvent(event);
                }
            } else {
                ChatUtil.log().info("[ChannelChat] Error Occured That Shouldn't Happen (chatListener.java)");
                event.setCancelled(true);
            }
        }
    }
}
