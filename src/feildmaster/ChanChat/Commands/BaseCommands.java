package feildmaster.ChanChat.Commands;

import feildmaster.ChanChat.Chan.Channel;
import feildmaster.ChanChat.Chan.ChannelManager;
import feildmaster.ChanChat.Util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


// TODO: admin commands
public abstract class BaseCommands implements ChatInterface {
    private final ChannelManager cm = ChatUtil.getChatPlugin().getCM();
    
    /*
     * "list" commands
     */
    protected void listChannels(Player player) {
        StringBuilder string = new StringBuilder();
        Boolean color = true;
        for(Channel chan : cm.getChannels())
            if(!chan.isPerm() && chan.getPass() == null && !chan.isMember(player))
                string.append(string.length()!=0?", ":"").append((color=!color)?ChatColor.GRAY:ChatColor.WHITE).append(chan.getName());
        player.sendMessage("Available Channels: "+string.toString());
    }
    
    /*
     * "quick chat"
     */
    protected void quickChat(String name, Player player, String[] args) {
        Channel chan = cm.getChannel(name);
        if(chan == null)
            player.sendMessage(ChatColor.YELLOW+"Channel does not exist");
        else if(chan.isMember(player)) {
            StringBuilder str = new StringBuilder();
            for(int x=1; x<args.length; x++)
                str.append(x!=1?" ":"").append(args[x]);
            cm.sendMessage(player, chan, str.toString());
        } else
            player.sendMessage(ChatColor.YELLOW+"You are not a member of \""+chan.getName()+".\"");
    }
    
    /*
     * "who" commands
     */
    protected void getChannelMembers(String name, Player player) {
        if(cm.channelExists(name) && cm.getChannel(name).isMember(player)) {
            StringBuilder string = new StringBuilder();
            for(Player p : cm.getChannel(name).getMembers())
                if(!p.equals(player))
                    string.append(string.length()!=0?", ":"").append(ChatColor.stripColor(p.getDisplayName()));
            if(string.length() == 0)
                string.append("Only you");
            player.sendMessage(string.insert(0, ChatColor.GRAY+"Members: ").toString());
        } else
            player.sendMessage(ChatColor.GRAY+"The channel doesn't exist!");
    }
    protected void getChannelMembers(Player player) {
        getChannelMembers(cm.getActiveName(player), player);
    }
    
    /*
     * "create" commands
     */
    protected void createChannel(String name, CommandSender sender) {
        if(sender instanceof Player)
            cm.addChannel(name, (Player)sender);
        else
            cm.addChannel(name, null, null, true);
    }
    
    /*
     * "delete" commands
     */
    protected void deleteActiveChannel(Player player) {
        deleteChannel(cm.getActiveName(player), player);
    }
    
    protected void deleteChannel(String name, CommandSender sender) {
        if(cm.channelExists(name)) {
            Channel chan = cm.getChannel(name);
            if(sender instanceof Player) {
                if (chan.isOwner((Player)sender) || ((Player)sender).hasPermission("ChanChat.admin")) {
                    chan.sendMessage("Has been deleted.");
                    cm.delChannel(name);
                } else 
                    sender.sendMessage("You can't do that.");
            } else {
                chan.sendMessage("Has been deleted.");
                cm.delChannel(name);
            }
        } else
            sender.sendMessage("The channel doesn't exists!");
    }
    
    /*
     * "join" commands
     */
    protected void joinChannel(String name, Player player) {
        if(cm.channelExists(name)) {
            Channel chan = cm.getChannel(name);
            if(chan.isMember(player))
                player.sendMessage(ChatColor.GRAY+"You are already in \""+chan.getName()+".\"");
            else {
                if(chan.getPass() != null) {
                    player.sendMessage(ChatColor.GRAY+"["+chan.getName()+"] Please enter the password");
                    cm.addToWaitlist(player, name);
                } else {
                    chan.addMember(player);
                    chan.sendMessage(ChatColor.YELLOW+ChatColor.stripColor(player.getDisplayName())+" has joined.");
                }
            }
        } else
            cm.addChannel(name, player);
    }
    
    /*
     * "leave" commands
     */
    protected void leaveChannel(String name, Player player) {
        if(name.equalsIgnoreCase("all"))
            leaveAll(player);
        else if(cm.channelExists(name))
            leaveChannel(player, cm.getChannel(name));
        else
            player.sendMessage(ChatColor.RED+"Channel \""+name+"\" doesn't exist.");
    }
    protected void leaveActiveChannel(Player player) {
        Channel chan = cm.getActiveChan(player);
        if(chan != null) {
            leaveChannel(player, chan);
        } else
            player.sendMessage("You are not in any channels to leave!");
    }
    private void leaveChannel(Player player, Channel chan) {
        if(chan.isMember(player)) {
            chan.sendMessage(ChatColor.YELLOW+ChatColor.stripColor(player.getDisplayName())+" has left.");
            chan.delMember(player);
        } else
            player.sendMessage(ChatColor.YELLOW+"You are not in \""+chan.getName()+".\"");
        cm.checkActive(player);
    }
    private void leaveAll(Player player) {
        for(Channel c : cm.getJoinedChannels(player))
            c.delMember(player);

        player.sendMessage(ChatColor.YELLOW+"You have left all channels.");
    }
    
    /*
     * "add" commands
     */
    protected void addPlayer(Player player, String i) {
        Player added = ChatUtil.getServer().getPlayer(i);
        Channel chan = cm.getActiveChan(player);
        if(chan.isMember(player) && !chan.isMember(added)) {
            chan.sendMessage(ChatColor.YELLOW+ChatColor.stripColor(added.getDisplayName())+
                    " has been added by "+ChatColor.stripColor(player.getDisplayName()));
            chan.addMember(added);
            added.sendMessage(ChatColor.YELLOW+"You have been added to \""+chan.getName()+".\"");
        } else if (chan.isMember(player) && chan.isMember(added))
            player.sendMessage(ChatColor.YELLOW+"Player is already in channel \""+chan.getName()+".\"");
        else
            player.sendMessage(ChatColor.RED+"Active channel not set, or you have not joined a channel.");
    }
    
    /*
     * "set" commands
     */
    protected void setChannel(String name, Player player) {
        if(cm.channelExists(name)) {
            Channel chan = cm.getChannel(name);
            if(chan.isMember(player)) {
                cm.setActiveByChan(player, chan);
                player.sendMessage(ChatColor.YELLOW+"Now talking in \""+chan.getName()+".\"");
            } else
                player.sendMessage(ChatColor.YELLOW+"You are not in \""+chan.getName()+".\"");
        } else
            player.sendMessage(ChatColor.RED+"Channel \""+name+"\" doesn't exist.");
    }
    
    /*
     * Booleans
     */   
    protected Boolean isReserved(String name) {
        return cm.isReserved(name);
    }
    protected Boolean channelExists(String name) {
        return cm.channelExists(name);
    }
    protected Boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }
    
    // TODO: function:reload
    protected Boolean reload() {
        
        return true;
    }
}
