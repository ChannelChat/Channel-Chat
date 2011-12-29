package com.feildmaster.channelchat.channel;

import com.feildmaster.channelchat.event.channel.*;
import com.feildmaster.channelchat.event.player.ChannelPlayerChatEvent;
import com.feildmaster.channelchat.channel.Channel.Type;
import static com.feildmaster.channelchat.Chat.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.AuthorNagException;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

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
    public Boolean isReserved(String n) {
        return n.matches("(?i)(active|add|all|create|delete|join|leave|list|reload|who|\\?)");
    }

    //Channel manager
    private List<Channel> registry = new ArrayList<Channel>();
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
     */
    public void delChannel(String name) {
        delChannel(getChannel(name));
    }

    public void delChannel(Channel channel) {
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
        return getActiveChannel(player).getName();
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

    // Event Factory
    private final Map<ChannelEvent.Type, SortedSet<RegisteredListener>> listeners = new EnumMap<ChannelEvent.Type, SortedSet<RegisteredListener>>(ChannelEvent.Type.class);
    private final Comparator<RegisteredListener> comparer = new Comparator<RegisteredListener>() {
        public int compare(RegisteredListener i, RegisteredListener j) {
            int result = i.getPriority().compareTo(j.getPriority());
            if ((result == 0) && (i != j)) result = 1;
            return result;
        }
    };

    public synchronized void callEvent(ChannelEvent event) {
        for (RegisteredListener registration : getEventListeners(event.getEventType())) {
            if(!registration.getPlugin().isEnabled()) continue;
            try {
                registration.callEvent(event);
            } catch (AuthorNagException ex) {
                Plugin plugin = registration.getPlugin();

                if (plugin.isNaggable()) {
                    plugin.setNaggable(false);

                    String author = "<NoAuthorGiven>";

                    if (plugin.getDescription().getAuthors().size() > 0)
                        author = plugin.getDescription().getAuthors().get(0);

                    plugin().getServer().getLogger().log(Level.SEVERE, String.format(
                        "Nag author: '%s' of '%s' about the following: %s",
                        author,
                        plugin.getDescription().getName(),
                        ex.getMessage()
                    ));
                }
            } catch (Throwable ex) {
                plugin().getServer().getLogger().log(Level.SEVERE, "Could not pass event " + event.getType() + " to " + registration.getPlugin().getDescription().getName(), ex);
            }
        }

        plugin().getServer().getPluginManager().callEvent(event); // Run through Bukkit's Event Handler!
    }
    public void registerEvent(ChannelEvent.Type type, ChannelListener listener, Event.Priority priority, Plugin plugin) {
        getEventListeners(type).add(new RegisteredListener(listener, new EventExecutor() {
            public void execute(Listener ll, Event event) {
                if(!(event instanceof ChannelEvent)) return;
                if(event instanceof ChannelCreateEvent)
                    ((ChannelListener)ll).onChannelCreate((ChannelCreateEvent)event);
                else if (event instanceof ChannelJoinEvent)
                    ((ChannelListener)ll).onChannelJoin((ChannelJoinEvent) event);
                else if (event instanceof ChannelLeaveEvent)
                    ((ChannelListener)ll).onChannelLeave((ChannelLeaveEvent) event);
                else if (event instanceof ReloadEvent)
                    ((ChannelListener)ll).onReload((ReloadEvent) event);
                else if (event instanceof SaveEvent)
                    ((ChannelListener)ll).onSave((SaveEvent) event);
            }
        }, priority, plugin));
    }

    private SortedSet<RegisteredListener> getEventListeners(ChannelEvent.Type type) {
        SortedSet<RegisteredListener> eventListeners = listeners.get(type);

        if(eventListeners != null) return eventListeners;

        eventListeners = new TreeSet<RegisteredListener>(comparer);
        listeners.put(type, eventListeners);

        return eventListeners;
    }

    public static ChannelManager getManager() {
        return manager;
    }
}
