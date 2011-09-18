package feildmaster.ChanChat.Listeners;

import feildmaster.ChanChat.Chan.*;
import feildmaster.ChanChat.Util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

public class PasswordListener extends PlayerListener {
    private ChannelManager cm = ChatUtil.getCM();

    public void onPlayerChat(PlayerChatEvent event) {
        if(event.isCancelled()) return;
        Player player = event.getPlayer();

        if(cm.inWaitlist(player)) {
            Channel chan =cm.getWaitingChan(player);
            if(chan.getPass().equals(event.getMessage())) {
                chan.addMember(player);
                chan.sendMessage(ChatColor.YELLOW+ChatColor.stripColor(player.getDisplayName())+" has joined.");
                cm.dfWaitlist(player);
            } else if(event.getMessage().equalsIgnoreCase("cancel")) {
                cm.dfWaitlist(player);
            } else
                player.sendMessage(ChatColor.GRAY+"Password incorrect, try again. (cancel to stop)");
            event.setCancelled(true);
        }
    }
}
