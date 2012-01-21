package com.feildmaster.channelchat.channel;

import com.feildmaster.channelchat.event.player.ChannelPlayerChatEvent;
import com.feildmaster.channelchat.channel.Channel.Type;
import static com.feildmaster.channelchat.Chat.*;
import java.util.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

public final class ChannelManager {
    private static final ChannelManager manager = new ChannelManager();

    ChannelManager() {}

    // TODO: Modularize
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
    public Boolean isChannelReserved(String n) {
        return n.matches("(?i)(active|add|all|create|delete|join|leave|list|reload|who|\\?)");
    }

    //Channel manager
    private List<Channel> registry = new LinkedList<Channel>();
    private Map<String, Channel> activeChannel = new HashMap<String, Channel>();

    // Message Handlers
    public void sendMessage(String channel, String msg) {sendMessage(getChannel(channel), msg);}
    public void sendMessage(Channel channel, String msg) {
        if(channel == null || msg == null) return;

        channel.sendMessage(" "+msg);
    }
    public void sendMessage(Player sender, String msg) {sendMessage(sender, getActiveChannel(sender), msg);}
    public void sendMessage(Player sender, String channel, String msg) {sendMessage(sender, getChannel(channel), msg);}
    public void sendMessage (Player sender, Channel channel, String msg) {
        if(channel == null || sender == null || msg == null) {
            sender.sendMessage(error("Missing info while trying to send message"));
            return;
        }

        ChannelPlayerChatEvent event = new ChannelPlayerChatEvent(sender, channel, msg);
        plugin().getServer().getPluginManager().callEvent(event);

        if(event.isCancelled()) return;

        msg = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());

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

        for(Channel chan : registry)
            if(chan.getName().equalsIgnoreCase(name) || (chan.getAlias() != null && chan.getAlias().equalsIgnoreCase(name)))
                return chan;

        return null;
    }

    /**
     * Grabs the channel from a chat event.
     *
     * @param event The event to extract the channel from
     * @return Channel tied to the event
     */
    public Channel getChannel(PlayerChatEvent event) {
        return event instanceof ChannelPlayerChatEvent ? ((ChannelPlayerChatEvent)event).getChannel() : getActiveChannel(event.getPlayer());
    }
    /**
     * @param name Name of Channel to check
     * @return True if channel is found
     */
    public Boolean channelExists(String name) {
        if(name == null) return false;

        for(Channel chan : registry)
            if(chan.getName().equalsIgnoreCase(name) || (chan.getAlias() != null && chan.getAlias().equalsIgnoreCase(name)))
                return true;

        return false;
    }
    public boolean channelExists(Channel channel) {
        return registry.contains(channel);
    }

    /**
     * Add a channel to the registry
     *
     * @param channel The channel to add
     */
    public boolean addChannel(Channel channel) {
        if(channel == null || registry.contains(channel)) return false;

        return registry.add(channel);
    }

    /**
     * Remove channel from registry
     *
     * @param name Name of channel to remove
     * @deprecated Use {@link deleteChannel}
     */
    @Deprecated
    public void delChannel(String name) {
        deleteChannel(name);
    }

    
    /**
     * Remove channel from registry
     *
     * @param Channel Channel to remove
     * @deprecated Use {@link deleteChannel(Channel)}
     */
    @Deprecated
    public void delChannel(Channel channel) {
        deleteChannel(channel);
    }

    /**
     * Remove channel from registry
     *
     * @param name Name of channel to remove
     */
    public void deleteChannel(String name) {
        deleteChannel(getChannel(name));
    }

    public void deleteChannel(Channel channel) {
        if(channel == null) return;
        if(registry.contains(channel)) {
            registry.remove(channel);
            channel.sendMessage(" Channel has been deleted");
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
    public Channel convertChannel(Channel channel, Type type) {
        if(channel.getType().equals(type) || channel.getType().equals(Type.Custom) || type.equals(Type.Custom))
            return channel;

        Channel chan1;

        if(type == Type.Local)
            chan1 = new LocalChannel(channel);
        else if (type == Type.World)
            chan1 = new WorldChannel(channel);
        else if (type == Type.Private)
            chan1 = new Channel(channel, Type.Private);
        else
            chan1 = new Channel(channel, Type.Global);

        if(registry.contains(channel)) {
            registry.remove(channel);
            registry.add(chan1);
        }

        return chan1;
    }

    // Active Channel Functions
    public String getActiveName(Player player) {
        Channel chan = getActiveChannel(player);
        if(chan == null) return null;
        
        return chan.getName();
    }
    public Channel getActiveChannel(Player player) {
        return activeChannel.get(player.getName());
    }
    public void setActiveChannel(Player player, Channel channel) {
        if (channel != null) {
            if(channelExists(channel) && channel.isMember(player))
                activeChannel.put(player.getName(), channel);
        } else {
            activeChannel.remove(player.getName());
        }
    }
    public void checkActive() {
        if(!activeChannel.isEmpty()) // Keyset errors on empty maps
        for(String name : activeChannel.keySet())
            checkActive(plugin().getServer().getPlayer(name));
    }
    public void checkActive(Player player) {
        if(player == null) return;

        Channel chan = getActiveChannel(player);

        if(!registry.contains(chan)) {
            setActiveChannel(player, null);
            chan = null;
        }

        List<Channel> joinedChannels = getJoinedChannels(player);

        // All is well with the world. :o
        if((chan == null && joinedChannels.isEmpty()) ||
                (channelExists(chan) && chan.isMember(player))) return;

        if (chan != null && joinedChannels.isEmpty()) {
            setActiveChannel(player, null);
        } else if (chan == null && !joinedChannels.isEmpty()) {
            setActiveChannel(player, joinedChannels.get(0));
        } else if (chan != null && channelExists(chan) && !chan.isMember(player)) {
            setActiveChannel(player, joinedChannels.get(0));
            player.sendMessage(info("You are now in channel \""+joinedChannels.get(0).getName()+".\""));
        }
    }

    public List<Channel> getJoinedChannels(Player player) {
        List<Channel> list = new ArrayList<Channel>();
        for (Channel chan : registry)
            if(chan.isMember(player))
                list.add(chan);
        return list;
    }
    public List<Channel> getChannels() {
        return new ArrayList<Channel>(registry);
    }
    public List<Channel> getAutoChannels() {
        List<Channel> list = new ArrayList<Channel>();
        for(Channel chan : registry)
            if(chan.isAuto())
                list.add(chan);
        return list;
    }
    public List<Channel> getSavedChannels() {
        List<Channel> list = new ArrayList<Channel>();
        for(Channel chan : registry)
            if(chan.isSaved())
                list.add(chan);
        return list;
    }

    public static ChannelManager getManager() {
        return manager;
    }
}
