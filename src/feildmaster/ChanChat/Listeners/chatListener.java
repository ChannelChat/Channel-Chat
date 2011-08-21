package feildmaster.ChanChat.Listeners;

import feildmaster.ChanChat.Chan.Channel;
import feildmaster.ChanChat.Chan.ChannelManager;
import feildmaster.ChanChat.Util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

/*
 * ChatListener
 *      Handles player chat
 */
public class chatListener extends PlayerListener {
    private final ChannelManager cm = ChatUtil.getChatPlugin().getCM();

    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        if(cm.inWaitlist(player)) {
            Channel chan =cm.getWaitingChan(player);
            if(chan.getPass().equals(event.getMessage())) {
                chan.addMember(player);
                chan.sendMessage(ChatColor.YELLOW+ChatColor.stripColor(player.getDisplayName())+" has joined.");
            } else if(event.getMessage().equalsIgnoreCase("cancel")) {
                cm.dfWaitlist(player);
            } else
                player.sendMessage("");
        } else if(cm.getJoinedChannels(player).isEmpty()) {
            player.sendMessage(ChatColor.YELLOW+"You are not in any channels.");
            event.setCancelled(true);
        } else {
            cm.checkActive(player);
            Channel chan = cm.getActiveChan(player);
            if(chan != null && cm.channelExists(chan.getName())) {
                cm.sendMessage(player, chan, event.getMessage());
                event.setCancelled(true);
            }
        }
    }
}
