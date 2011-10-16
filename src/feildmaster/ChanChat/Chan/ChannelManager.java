package feildmaster.ChanChat.Chan;

import feildmaster.ChanChat.Events.ChannelPlayerChatEvent;
import feildmaster.ChanChat.Util.ChatUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;

public class ChannelManager {
    private Map<Player, String> waitList = new HashMap<Player, String>();

    //Waitlist functions
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

    //Reserved name settings/functions
    private static final String[] reserved = { "active", "admin", "all", "create", "delete", "add", "join",
        "kick", "leave", "list", "reload", "who", "?" };

    public Boolean isReserved(String n) {
        return Arrays.asList(reserved).contains(n);
    }

    //Channel manager
    private List<Channel> registry = new ArrayList<Channel>();
    private Map<String, String> activeChannel = new HashMap<String, String>();

    // Message Handlers
    public void sendMessage(Player sender, String msg) {sendMessage(sender, getActiveChan(sender), msg);}
    public void sendMessage(Player sender, String channel, String msg) {sendMessage(sender, getChannel(channel), msg);}
    public void sendMessage (Player sender, Channel channel, String msg) {
        if(channel == null || sender == null || msg == null) {
            sender.sendMessage(ChatUtil.error("Missing info while trying to send message"));
            return;
        }

        ChannelPlayerChatEvent event = new ChannelPlayerChatEvent(sender, channel, msg);
        ChatUtil.getPluginManager().callEvent(event);

        if(event.isCancelled()) return;

        msg = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
        //((CraftServer)getServer()).getServer().console.sendMessage(s); // ColorConsoleSender
        System.out.println(msg);
        for(Player p1 : event.getRecipients())
            p1.sendMessage(msg);
    }

    // Channel Functions
    /**
     * @param name of Channel to return
     * @return Channel if exists, else NULL
     */
    public Channel getChannel(String name) {
        if(name == null) return null;

        for(Channel chan : getChannels())
            if(chan.getName().equalsIgnoreCase(name))
                return chan;

        return null;
    }
    /**
     * @param name Name of Channel to check
     * @return True if channel is found
     */
    public Boolean channelExists(String name) {
        if(name != null)
          for(Channel chan : getChannels())
            if(chan.getName().equalsIgnoreCase(name))
                return true;

        return false;
    }
    /**
     * Player Adding Channel
     *
     * @param name Name of Channel
     * @param player Owner of Channel
     */
    public void addChannel(String name, Player player) { // Player adding channel
        if(isReserved(name)) {
            player.sendMessage(ChatUtil.error("["+name+"] Blacklisted."));
            return;
        }

        if(!channelExists(name)) {
            Channel chan = new Channel(name);
            chan.setOwner(player);
            chan.addMember(player);
            getChannels().add(chan);
            chan.sendMessage("Created!");
            if(getActiveName(player) == null)
                setActiveChan(player,name);
        } else
            player.sendMessage(ChatUtil.info("["+name+"] Already exists!"));
    }

    /**
     * Add a channel to the registry
     *
     * @param chan The channel to add
     */
    public void addChannel(Channel chan) {
        if(!channelExists(chan.getName()))
            getChannels().add(chan);
    }
    /**
     * Add a channel to the registry, with the player owner
     *
     * @param chan The channel to add
     */
    public void addChannel(Channel chan, Player player) {
        if(!channelExists(chan.getName())) {
            addChannel(chan);
            chan.setOwner(player);
            if(getActiveName(player) == null)
                setActiveChan(player, chan.getName());
        }
    }

    /**
     * Remove channel from registry
     *
     * @param name Name of channel to remove
     */
    public void delChannel(String name) {
        if(channelExists(name)) {
            getChannel(name).sendMessage("Has been deleted");
            getChannels().remove(getChannel(name));
            checkActive();
        }
    }

    // Active Channel Functions
    public String getActiveName(Player player) {
        return activeChannel.get(player.getName());
    }
    public Channel getActiveChan(Player player) {
        return getChannel(getActiveName(player));
    }
    public void setActiveChan(Player player, String channel) {
        if (channel != null) {
            if(channelExists(channel) && getChannel(channel).isMember(player))
                activeChannel.put(player.getName(), channel);
        } else {
            activeChannel.remove(player.getName());
        }
    }
    public void setActiveByChan(Player player, Channel chan) {
        setActiveChan(player, chan.getName());
    }
    public void checkActive() {
        if(!activeChannel.isEmpty()) // Keyset errors on empty maps
        for(String name : activeChannel.keySet())
            checkActive(ChatUtil.getPlayer(name));
    }
    public void checkActive(Player player) {
        if(player == null) return;

        String channel = getActiveName(player);

        // All is well with the world. :o
        if((channel == null && getJoinedChannels(player).isEmpty()) ||
                (channelExists(channel) && getChannel(channel).isMember(player))) return;

        if (channel != null && getJoinedChannels(player).isEmpty()) {
            setActiveChan(player, null);
        } else if (channel == null && !getJoinedChannels(player).isEmpty()) {
            setActiveChan(player, getJoinedChannels(player).get(0).getName());
        } else if (channel != null && channelExists(channel) && !getChannel(channel).isMember(player)) {
            String nChan = getJoinedChannels(player).get(0).getName();
            setActiveChan(player, nChan);
            player.sendMessage(ChatUtil.info("You are now in channel \""+nChan+".\""));
        }
    }

    // Player Functions
    public List<Channel> getJoinedChannels(Player player) {
        List<Channel> list = new ArrayList<Channel>();
        for (Channel chan : getChannels())
            if(chan.isMember(player))
                list.add(chan);
        return list;
    }

    public List<Channel> getChannels() {
        return registry;
    }
    public List<Channel> getAutoChannels() {
        List<Channel> list = new ArrayList<Channel>();
        for(Channel chan : getChannels())
            if(chan.isAuto())
                list.add(chan);
        return list;
    }
    public List<Channel> getSavedChannels() {
        List<Channel> list = new ArrayList<Channel>();
        for(Channel chan : getChannels())
            if(chan.isSaved())
                list.add(chan);
        return list;
    }
}
