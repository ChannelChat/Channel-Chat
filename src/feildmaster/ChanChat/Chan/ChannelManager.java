package feildmaster.ChanChat.Chan;

import feildmaster.ChanChat.Chat;
import feildmaster.ChanChat.Util.ChatUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChannelManager {
    private final Chat plugin = ChatUtil.getChatPlugin();
    private Map<Player, String> waitList = new HashMap<Player, String>();
    /*
     * Waitlist functions
     */
    public void dfWaitlist(Player player) {
        waitList.remove(player);
    }
    public void addToWaitlist(Player player, String chan) {
        waitList.put(player, chan);
    }
    public Boolean inWaitlist(Player player) {
        return waitList.containsKey(player);
    }
    public Channel getWaitingChan(Player player) {
        return getChannel(waitList.get(player));
    }
    /*
     * Reserved name settings/functions
     */
    private static final String[] reserved = { "admin", "all", "create", "delete", "add", "join",
        "kick", "leave", "list", "reload", "who" };

    public Boolean isReserved(String n) {
        for(String r : reserved)
            if(n.equalsIgnoreCase(r))
                return true;
        
        return false;
    }
    //public String[] getReserved() {return reserved;}
    public String getReservedString() {
        String list = "";
        for(int x=0; x<reserved.length; x++)
            if(x!=1) // "all" isn't a command.
            list += (x!=0?", ":"")+reserved[x];
        return list;
    }
    
    /*
     * Channel manager
     */
    private List<Channel> registry = new ArrayList<Channel>();
    private Map<String, String> activeChannel = new HashMap<String, String>();

    // Message Handlers
    public void sendMessage(Player sender, String msg) {sendMessage(sender, getActiveChan(sender), msg);}
    public void sendMessage(Player sender, String channel, String msg) {sendMessage(sender, getChannel(channel), msg);}
    public void sendMessage (Player sender, Channel channel, String msg) {
        channel.sendMessage(processMessage(sender, channel, msg));
    }
    private String processMessage(Player sender, Channel channel, String msg) {
        // Gets name, tags, and returns a message string
        return sender.getDisplayName()+": "+msg;
    }

    // Channel Functions
    public Channel getChannel(String name) {
        if(name == null || registry.isEmpty()) return null;

        for(Channel chan : registry)
            if(chan.getName().equalsIgnoreCase(name))
                return chan;

        return null;
    }
    public Boolean channelExists(String name) {
        if(name != null && !registry.isEmpty())
          for(Channel chan : registry)
            if(chan.getName().equalsIgnoreCase(name))
                return true;

        return false;
    }
    public void addChannel(String name, Player player) { // Player adding channel
        if(isReserved(name)) {
            player.sendMessage(ChatColor.RED+"Channel \""+name+"\" is blacklisted.");
            return;
        }

        if(!channelExists(name)) {
            Channel chan = new Channel();
            chan.setName(name);
            chan.setOwner(player);
            chan.addMember(player);
            registry.add(chan);
            player.sendMessage(ChatColor.YELLOW+name+" created!");
            if(getActiveName(player) == null) {
                setActiveChan(player,name);
            }
        } else {
            player.sendMessage(ChatColor.YELLOW+"Channel \""+name+"\" already exists!");
        }
    }

    public void addChannel(String name, String owner, String pass, Boolean alert) { // Server adding channel
        if(isReserved(name)) {
            System.out.println(ChatColor.RED+"Channel \""+name+"\" is blacklisted.");
            return;
        }

        if(!channelExists(name)) {
            Channel chan = new Channel();
            chan.setName(name);
            chan.setOwner(owner);
            registry.add(chan);
            if(alert)
                System.out.println(name+" created!");
        } else
            System.out.println("Channel \""+getChannel(name).getName()+"\" already exists!");
    }

    public void delChannel(String name) {
        if(channelExists(name)) {
            for(Player player : getChannel(name).getMembers()) {
                player.sendMessage(ChatColor.YELLOW + name + " has been deleted.");
                checkActive(player);
            }
            registry.remove(getChannel(name));
        }
    }

    // Active Channel Function
    public String getActiveName(Player player) {
        return activeChannel.get(player.getName());
    }

    public Channel getActiveChan(Player player) {
        return getChannel(activeChannel.get(player.getName()));
    }
    public void setActiveChan(Player player, String channel) {
        if (channel != null) {
            if(channelExists(channel) && getChannel(channel).isMember(player))
                activeChannel.put(player.getName(), channel);
            else
                player.sendMessage("Something went wrong!");
        } else {
            activeChannel.remove(player.getName());
        }
    }
    public void setActiveByChan(Player player, Channel chan) {
        String cName = chan.getName();
        if (chan != null) {
            if(channelExists(cName) && chan.isMember(player))
                activeChannel.put(player.getName(), cName);
        } else {
            activeChannel.remove(player.getName());
        }
    }
    public void checkActive() {
        for(String name : activeChannel.keySet()) {
            Player player = plugin.getServer().getPlayer(name);
            checkActive(player);
        }
    }
    public void checkActive(Player player) {
        String channel = getActiveName(player);
        if (channel != null && getJoinedChannels(player).isEmpty()) {
            setActiveChan(player, null);
        } else if (channel == null && !getJoinedChannels(player).isEmpty()) {
            setActiveChan(player, getJoinedChannels(player).get(0).getName());
        } else if ((channel != null && channelExists(channel)
                && !getChannel(channel).isMember(player))) {
            String nChan = getJoinedChannels(player).get(0).getName();
            setActiveChan(player, nChan);
            player.sendMessage(ChatColor.YELLOW+"You are now in channel \""+nChan+".\"");
        }
    }

    // Player Functions
    public List<Channel> getChannels() {
        return registry;
    }
    public List<Channel> getJoinedChannels(Player player) {
        List<Channel> list = new ArrayList<Channel>();

        for (Channel chan : registry)
            if(chan.isMember(player))
                list.add(chan);

        return list;
    }
}
