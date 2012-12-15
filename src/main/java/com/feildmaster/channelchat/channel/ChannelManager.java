package com.feildmaster.channelchat.channel;

import com.feildmaster.channelchat.event.player.ChannelPlayerChatEvent;
import com.feildmaster.channelchat.channel.Channel.Type;
import static com.feildmaster.channelchat.Chat.*;
//import com.feildmaster.channelchat.command.CommandManager;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class ChannelManager {
    private static final ChannelManager manager = new ChannelManager();
    //private final CommandManager commandManager = new CommandManager();
    //Channel manager
    private final List<Channel> registry = Collections.synchronizedList(new LinkedList<Channel>());
    private final Map<String, Channel> activeChannel = new ConcurrentHashMap<String, Channel>();

    ChannelManager() {
    }
    // TODO: Modularize
    private Map<Player, String> waitList = new ConcurrentHashMap<Player, String>();

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

    // Message Handlers
    public void sendMessage(String channel, String msg) {
        sendMessage(getChannel(channel), msg);
    }

    public void sendMessage(Channel channel, String msg) {
        if (channel == null || msg == null || msg.length() == 0) {
            return;
        }

        channel.sendMessage(" " + msg);
    }

    public void sendMessage(Player sender, String msg) {
        sendMessage(sender, getActiveChannel(sender), msg);
    }

    public void sendMessage(Player sender, String channel, String msg) {
        sendMessage(sender, getChannel(channel), msg);
    }

    public void sendMessage(Player sender, Channel channel, String msg) {
        sendMessage(sender, channel, msg, !Bukkit.isPrimaryThread());
    }

    public void sendMessage(Player sender, Channel channel, String msg, boolean async) {
        if (channel == null || sender == null || msg == null) {
            sender.sendMessage(error("Missing info while trying to send message"));
            return;
        }

        Set players = new HashSet();
        Collections.addAll(players, sender.getServer().getOnlinePlayers());

        ChannelPlayerChatEvent event = new ChannelPlayerChatEvent(sender, channel, msg, players, async);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        msg = String.format(event.getFormat(), sender.getDisplayName(), event.getMessage());

        System.out.println(ChatColor.stripColor(msg));
        for (Player p : event.getRecipients()) {
            p.sendMessage(msg);
        }
    }

    // Channel Functions
    /**
     * @param name Name or Alias of Channel to return
     * @return Channel if exists, else NULL
     */
    public Channel getChannel(String name) {
        if (name == null) {
            return null;
        }

        for (Channel chan : registry) {
            if (chan.getName().equalsIgnoreCase(name) || (chan.getAlias() != null && chan.getAlias().equalsIgnoreCase(name))) {
                return chan;
            }
        }

        return null;
    }

    /**
     * Grabs the channel from a chat event.
     *
     * @param event The event to extract the channel from
     * @return Channel tied to the event
     */
    public Channel getChannel(AsyncPlayerChatEvent event) {
        return event instanceof ChannelPlayerChatEvent ? ((ChannelPlayerChatEvent) event).getChannel() : getActiveChannel(event.getPlayer());
    }

    /**
     * @param name Name of Channel to check
     * @return True if channel is found
     */
    public Boolean channelExists(String name) {
        if (name == null) {
            return false;
        }

        for (Channel chan : registry) {
            if (chan.getName().equalsIgnoreCase(name) || (chan.getAlias() != null && chan.getAlias().equalsIgnoreCase(name))) {
                return true;
            }
        }

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
        if (channel == null || registry.contains(channel)) {
            return false;
        }

        return registry.add(channel);
    }

    /**
     * Remove channel from registry
     *
     * @param name Name of channel to remove
     */
    @Deprecated
    public void deleteChannel(String name) {
        deleteChannel(getChannel(name));
    }

    @Deprecated
    public void deleteChannel(Channel channel) {
        if (channel == null) {
            return;
        }
        removeChannel(channel).sendMessage(" Channel has been deleted");
        checkActive();
    }

    public Channel removeChannel(String name) {
        return removeChannel(getChannel(name));
    }

    public Channel removeChannel(Channel channel) {
        if (channel != null && registry.contains(channel)) {
            registry.remove(channel);
        }

        return channel;
    }

    public Channel createChannel(String name, Type type) {
        Channel chan;

        if (type == Type.Local) {
            chan = new LocalChannel(name);
        } else if (type == Type.World) {
            chan = new WorldChannel(name);
        } else if (type == Type.Private) {
            chan = new Channel(name, Type.Private);
        } else {
            chan = new Channel(name, Type.Global);
        }

        return chan;
    }

    /**
     * Usage of this function should be limited
     */
    public Channel convertChannel(Channel channel, Type type) {
        if (channel.getType().equals(type) || channel.getType().equals(Type.Custom) || type.equals(Type.Custom)) {
            return channel;
        }

        Channel chan1;

        if (type == Type.Local) {
            chan1 = new LocalChannel(channel);
        } else if (type == Type.World) {
            chan1 = new WorldChannel(channel);
        } else if (type == Type.Private) {
            chan1 = new Channel(channel, Type.Private);
        } else {
            chan1 = new Channel(channel, Type.Global);
        }

        if (registry.contains(channel)) {
            registry.remove(channel);
            registry.add(chan1);
        }

        return chan1;
    }

    // Active Channel Functions
    public String getActiveName(Player player) {
        Channel chan = getActiveChannel(player);
        return chan == null ? null : chan.getName();
    }

    public boolean hasActiveChannel(Player player) {
        return channelExists(activeChannel.get(player.getName()));
    }

    public Channel getActiveChannel(Player player) {
        Channel active = activeChannel.get(player.getName());
        if (channelExists(active)) {
            return active;
        }

        setActiveChannel(player, null);
        return null;
    }

    public void setActiveChannel(Player player, Channel channel) {
        if (channel == null || (channelExists(channel) && channel.isMember(player))) {
            activeChannel.put(player.getName(), channel);
        }
    }

    public void checkActive() {
        Iterator<String> iterator = activeChannel.keySet().iterator();
        while (iterator.hasNext()) {
            checkActive(Bukkit.getServer().getPlayer(iterator.next()));
        }
    }

    public void checkActive(Player player) {
        if (player == null) {
            return;
        }

        Channel chan = getActiveChannel(player);

        if (!registry.contains(chan)) {
            setActiveChannel(player, null);
            chan = null;
        }

        List<Channel> joinedChannels = getJoinedChannels(player);

        // All is well with the world. :o
        if ((chan == null && joinedChannels.isEmpty())
                || (channelExists(chan) && chan.isMember(player))) {
            return;
        }

        if (chan != null && joinedChannels.isEmpty()) {
            setActiveChannel(player, null);
        } else if (chan == null && !joinedChannels.isEmpty()) {
            setActiveChannel(player, joinedChannels.get(0));
        } else if (chan != null && channelExists(chan) && !chan.isMember(player)) {
            setActiveChannel(player, joinedChannels.get(0));
            player.sendMessage(info("You are now speaking in \"" + joinedChannels.get(0).getName() + ".\""));
        }
    }

    public List<Channel> getJoinedChannels(Player player) {
        List<Channel> list = new ArrayList<Channel>();
        for (Channel chan : registry) {
            if (chan.isMember(player)) {
                list.add(chan);
            }
        }
        return list;
    }

    public List<Channel> getChannels() {
        return new ArrayList<Channel>(registry);
    }

    public List<Channel> getAutoChannels() {
        List<Channel> list = new ArrayList<Channel>();
        for (Channel chan : registry) {
            if (chan.isAuto()) {
                list.add(chan);
            }
        }
        return list;
    }

    /**
     * Auto join channels if persist allows it.
     *
     * @param player Player to add to channels
     */
    public void joinAutoChannels(Player player) {
        if ((!plugin().getConfig().persistRelog()) || (plugin().getConfig().persistRelog() && !hasActiveChannel(player))) {
            for (Channel chan : getAutoChannels()) {
                chan.addMember(player);
            }
        }
    }

    public List<Channel> getSavedChannels() {
        List<Channel> list = new ArrayList<Channel>();
        for (Channel chan : registry) {
            if (chan.isSaved()) {
                list.add(chan);
            }
        }
        return list;
    }

    public static ChannelManager getManager() {
        return manager;
    }
//
//    public CommandManager getCommandManager() {
//        return commandManager;
//    }
}
