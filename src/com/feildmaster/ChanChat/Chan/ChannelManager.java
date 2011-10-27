package com.feildmaster.chanchat.Chan;

import com.feildmaster.chanchat.Chan.Channel.Type;
import com.feildmaster.chanchat.Events.ChannelPlayerChatEvent;
import com.feildmaster.chanchat.Util.ChatUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;

public final class ChannelManager {
    private Map<Player, String> waitList = new HashMap<Player, String>();

    // Waitlist functions
    public void deleteFromWaitlist(Player player) {
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

    // Reserved name settings/functions
    public Boolean isReserved(String n) {
        return n.matches("(?i)(active|add|admin|all|create|delete|join|leave|list|reload|who|\\?)");
    }

    //Channel manager
    private List<Channel> registry = new ArrayList<Channel>();
    private Map<String, String> activeChannel = new HashMap<String, String>();
    private int ownerLimit = -1;

    // Message Handlers
    public void sendMessage(String channel, String msg) {sendMessage(getChannel(channel), msg);}
    public void sendMessage(Channel channel, String msg) {
        if(channel == null || msg == null) return;

        channel.sendMessage(msg);
    }
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
        System.out.println(msg.replaceAll("\u00A7.", ""));
        for(Player p : event.getRecipients())
            p.sendMessage(msg);
    }

    // Channel Functions
    /**
     * @param name Name or Alias of Channel to return
     * @return Channel if exists, else NULL
     */
    public Channel getChannel(String name) {
        if(name == null) return null;

        for(Channel chan : getChannels())
            if(chan.getName().equalsIgnoreCase(name) || (chan.getAlias() != null && chan.getAlias().equalsIgnoreCase(name)))
                return chan;

        return null;
    }
    /**
     * @param name Name of Channel to check
     * @return True if channel is found
     */
    public Boolean channelExists(String name) {
        if(name == null) return false;

        for(Channel chan : getChannels())
            if(chan.getName().equalsIgnoreCase(name) || (chan.getAlias() != null && chan.getAlias().equalsIgnoreCase(name)))
                return true;

        return false;
    }
    /**
     * Add a channel to the registry
     *
     * @param chan The channel to add
     */
    public void addChannel(Channel chan) {
        // TODO: Message? No. Not with the current system
        if(channelExists(chan.getName())) return;

        registry.add(chan);
    }
    /**
     * Remove channel from registry
     *
     * @param name Name of channel to remove
     */
    public void delChannel(String name) {
        // TODO: Channels Disposable, Does it effect usage?
        if(channelExists(name)) {
            Channel chan = getChannel(name);
            sendMessage(chan, " Channel has been deleted");
            registry.remove(chan);
            checkActive();
        }
    }

    public Channel createChannel(String name, Type type) {
        Channel chan;

        if(type == Type.Local)
            chan = new LocalChannel(name);
        else if (type == Type.World)
            chan = new WorldChannel(name);
        else if (type == Type.Private)
            chan = new Channel(name, Type.Private);
        else
            chan = new Channel(name, Type.Global);

        return chan;
    }

    /**
     * Usage of this funciton should be limited
     */
    public Channel convertChannel(Channel chan, Type type) {
        if(chan.getType().equals(type) || chan.getType().equals(Type.Custom) || type.equals(Type.Custom))
            return chan;

        Channel chan1;

        if(type == Type.Local)
            chan1 = new LocalChannel(chan);
        else if (type == Type.World)
            chan1 = new WorldChannel(chan);
        else if (type == Type.Private)
            chan1 = new Channel(chan, Type.Private);
        else
            chan1 = new Channel(chan, Type.Global);

        if(registry.contains(chan)) {
            registry.remove(chan);
            registry.add(chan1);
        }

        return chan1;
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

        List<Channel> joinedChannels = getJoinedChannels(player);

        // All is well with the world. :o
        if((channel == null && joinedChannels.isEmpty()) ||
                (channelExists(channel) && getChannel(channel).isMember(player))) return;

        if (channel != null && joinedChannels.isEmpty()) {
            setActiveChan(player, null);
        } else if (channel == null && !joinedChannels.isEmpty()) {
            setActiveChan(player, joinedChannels.get(0).getName());
        } else if (channel != null && channelExists(channel) && !getChannel(channel).isMember(player)) {
            String nChan = joinedChannels.get(0).getName();
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
        return new ArrayList<Channel>(registry);
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
