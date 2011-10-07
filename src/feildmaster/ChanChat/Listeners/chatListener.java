package feildmaster.ChanChat.Listeners;

import feildmaster.ChanChat.Chan.Channel;
import feildmaster.ChanChat.Chan.ChannelManager;
import feildmaster.ChanChat.Events.ChannelPlayerChatEvent;
import feildmaster.ChanChat.Util.ChatUtil;
import java.util.HashSet;
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
            Channel chan = null;

            if(event instanceof ChannelPlayerChatEvent) {
                chan = ((ChannelPlayerChatEvent)event).getChannel();
            } else
                chan = cm.getActiveChan(player);

            if(chan != null) {
                boolean local = chan.getType().equals(Channel.Type.Local);
                boolean world = chan.getType().equals(Channel.Type.World);
                if(local || world) {
                    if(world && chan.getTag().equals("{World}")) chan.setTag(player.getWorld().getName());
                    for(Player p : new HashSet<Player>(event.getRecipients()))
                        if(!p.getWorld().equals(player.getWorld()) || (local && chan.outOfRange(p.getLocation(),player.getLocation())))
                            event.getRecipients().remove(p);
                //} else if (chan.getType().equals(Channel.Type.Faction)) {
                } else // Fall back on normal channel
                    chan.handleEvent(event);
            } else {
                ChatUtil.log().info("[ChannelChat] Error Occured That Shouldn't Happen (chatListener.java)");
                event.setCancelled(true);
            }
        }
    }
}
