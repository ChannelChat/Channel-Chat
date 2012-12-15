package com.feildmaster.channelchat.channel;

import static com.feildmaster.channelchat.channel.ChannelManager.getManager;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Channel {
    private final String name;
    private final Type type;
    private String alias = null;
    private String tag = null;
    private String owner = null;
    private String pass = null;
    private Boolean auto_join = false;
    private Boolean listed = false;
    private final Map<String, Boolean> members = new ConcurrentHashMap<String, Boolean>();

    public enum Type {
        Global,
        World,
        Local,
        Private,
        Custom;

        public boolean isGlobal() {
            return this == Global;
        }

        public boolean isWorld() {
            return this == World;
        }

        public boolean isLocal() {
            return this == Local;
        }

        public boolean isPrivate() {
            return this == Private;
        }

        public boolean isCustom() {
            return this == Custom;
        }

        private boolean isSaved() {
            return !this.equals(Private) && !this.equals(Custom);
        }
        static final List<String> list = new ArrayList<String>();

        public static boolean contains(String name) {
            return list.contains(name);
        }

        public static Type betterValueOf(String name) {
            if (name != null) {
                for (Type t : values()) {
                    if (t.name().equals(name)) {
                        return t;
                    }
                }
            }

            return Type.Global;
        }

        static {
            for (Type t : values()) {
                list.add(t.name());
            }
        }
    }

    protected Channel(Channel c, Type t) {
        name = c.name;
        type = t;
        tag = c.tag;
        owner = c.owner;
        pass = c.pass;
        auto_join = c.auto_join;
        listed = c.listed;
        members.putAll(c.members);
    }

    protected Channel(String n, Type t) {
        name = n;
        type = t;
    }

    // Send messages to channel
    public final void sendMessage(String msg) {
        if (members.isEmpty()) {
            return;
        }

        msg = format(msg);

        System.out.print(ChatColor.stripColor(msg));

        for (String n : members.keySet()) {
            Player p = Bukkit.getPlayer(n);
            if (isMember(p)) { // Check "is member" in case of an override
                p.sendMessage(msg);
            }
        }
    }

    public void sendJoinMessage(Player player) {
        sendMessage(" " + ChatColor.YELLOW + ChatColor.stripColor(player.getDisplayName()) + " has joined.");
    }

    public void sendLeaveMessage(Player player) {
        sendMessage(" " + ChatColor.YELLOW + ChatColor.stripColor(player.getDisplayName()) + " has left.");
    }

    // Channel Formatting
    public final String format(String old) {
        return getDisplayName() + (old.equals("<%1$s> %2$s") ? " " : "") + old;
    }

    public String getDisplayName() {
        return tag == null ? "[" + name + "]" : (tag.replaceAll("(?i)`(?=[0-9A-F])", "\u00A7") + ChatColor.WHITE);
    }

    public final String getName() {
        return name;
    }

    public final Type getType() {
        return type;
    }

    public final String getAlias() {
        return alias;
    }

    public final boolean setAlias(String s) {
        if (alias != null && alias.equals(s)) {
            return true;
        }

        if (s != null && getManager().getChannel(s) != null) {
            return false;
        }

        alias = s;

        return true;
    }

    // Tag functions
    public final void setTag(String t) {
        tag = t;
    }

    public final String getTag() {
        return tag;
    }

    // Owner Functions
    public String getOwner() {
        return owner;
    }

    public void setOwner(Player player) {
        setOwner(player.getName());
    }

    public void setOwner(String name) {
        owner = name;
    }

    public Boolean isOwner(Player player) {
        if (owner == null || owner.length() == 0) {
            return false;
        }
        return owner.equalsIgnoreCase(player.getName());
    }

    // Member Functions - For use with /cc who
    public Set<String> getMembers(Player player) {
        return new HashSet<String>(members.keySet());
    }

    // If member functions
    public Boolean isSenderMember(Player player) {
        //return player.hasPermission("ChanChat.admin") || isMember(player);
        return isMember(player);
    }

    public Boolean isMember(Player player) {
        if (player == null) {
            return false;
        }
        return isMember(player.getName());
    }

    private Boolean isMember(String player) {
        return members.containsKey(player);
    }

    // Add members
    public final void addMember(Player player) {
        addMember(player, false);
    }

    public final void addMember(Player player, Boolean alert) {
        if (player == null) {
            return;
        }

        addMember(player.getName());

        if (alert) {
            sendJoinMessage(player);
        }
    }

    private void addMember(String player) {
        members.put(player, true);
    }

    // Remove members
    public final void delMember(Player player) {
        delMember(player, false);
    }

    public final void delMember(Player player, Boolean alert) {
        if (player == null || !isMember(player)) {
            return;
        }

        if (alert) {
            sendLeaveMessage(player);
        }

        delMember(player.getName());
    }

    private void delMember(String player) {
        members.remove(player);
    }

    // Password Functions
    public final void removePass() {
        setPass(null);
    }

    public final String getPass() {
        return pass;
    }

    public final void setPass(String p) {
        pass = p;
    }

    // Listed functions
    public final void setListed(Boolean l) {
        listed = l;
    }

    public final Boolean isListed() {
        return listed;
    }

    // Auto functions
    public final void setAuto(Boolean v) {
        auto_join = v;
    }

    public final Boolean isAuto() {
        return auto_join;
    }

    // Various methods
    public void handleEvent(AsyncPlayerChatEvent event) {
        if (isSenderMember(event.getPlayer())) {
            for (Player p : new HashSet<Player>(event.getRecipients())) {
                if (!isMember(p)) {
                    event.getRecipients().remove(p);
                }
            }
            // !!! I want to format outside of the handleEvent...
            event.setFormat(format(event.getFormat()));
        } else {
            event.getPlayer().sendMessage(format(" Not a member"));
            event.setCancelled(true);
        }
    }

    // Per-Channel "can join" checks.
    public boolean canJoin(Player player) {
        return true;
    }

    // Lovely Booleans
    public final boolean isSaved() {
        return type.isSaved();
    }

    @Override
    public String toString() {
        return getName() + " [" + getTag() + "] (" + getType() + ")";
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap();

        map.put("type", getType().name());
        map.put("tag", getTag());
        map.put("owner", getOwner());
        map.put("password", getPass());
        map.put("listed", isListed());
        map.put("auto_join", isAuto());
        map.put("alias", getAlias());

        return map;
    }
}
