package feildmaster.ChanChat.Listeners;

import feildmaster.ChanChat.Chan.Channel;
import feildmaster.ChanChat.Chan.ChannelManager;
import feildmaster.ChanChat.Util.ChatUtil;
import java.util.HashSet;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

/*
 * ChatListener
 *      Handles player chat
 */
public class chatListener extends PlayerListener {
    private final ChannelManager cm = ChatUtil.getCM();

    public void onPlayerChat(PlayerChatEvent event) {
        if(event.isCancelled()) return;
        Player player = event.getPlayer();

        if(ChatUtil.getFactionPlugin() != null && ChatUtil.getFactionPlugin().isPlayerFactionChatting(player))
            return;

        if(cm.inWaitlist(player)) {
            Channel chan =cm.getWaitingChan(player);
            if(chan.getPass().equals(event.getMessage())) {
                chan.addMember(player);
                chan.sendMessage(ChatColor.YELLOW+ChatColor.stripColor(player.getDisplayName())+" has joined.");
            } else if(event.getMessage().equalsIgnoreCase("cancel")) {
                cm.dfWaitlist(player);
            } else
                player.sendMessage(ChatColor.GRAY+"Password incorrect, try again. (cancel to stop)");
            event.setCancelled(true);
        } else if(cm.getJoinedChannels(player).isEmpty()) {
            player.sendMessage(ChatColor.YELLOW+"You are not in any channels.");
            event.setCancelled(true);
        } else {
            cm.checkActive(player);
            Channel chan = cm.getActiveChan(player);

            if(chan != null && chan.isMember(player)) {
                for(Player p : new HashSet<Player>(event.getRecipients()))
                    if(!chan.isMember(p))
                        event.getRecipients().remove(p);

                event.setFormat(chan.format(event.getFormat()));
            } else
                event.setCancelled(true);
        }
    }
}
