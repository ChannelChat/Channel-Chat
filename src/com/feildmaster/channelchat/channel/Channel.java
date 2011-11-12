package com.feildmaster.channelchat.channel;

import static com.feildmaster.channelchat.channel.ChannelManager.getManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

// TODO: AutoJoin on ChannelChange...
// TODO: Channel Joining based on Permissions? Channel Permissions?
@org.bukkit.configuration.serialization.SerializableAs("ChanChat-Channel")
public class Channel implements ConfigurationSerializable {
    private final String name;
    private final Type type;
    private String alias = null;
    private String tag = null;
    private String owner = null;
    private String pass = null;
    private Boolean auto_join = false;
    private Boolean listed = false;
    private Set<String> members = new HashSet<String>();

    public enum Type {
        Global,
        World,
        Local,
        Private,
        Custom;

        static final List<String> list = new ArrayList<String>();

        public static boolean contains(String name) {
            return list.contains(name);
        }

        public static Type betterValueOf(String name) {
            if(name != null)
                for(Type t : values())
                    if(t.name().equals(name))
                        return t;

            return Type.Global;
        }

        static {
            for(Type t : values())
                list.add(t.name());
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
        members = c.members;
    }

    protected Channel(String n, Type t) {
        name = n;
        type = t;
    }

    // Send messages to channel
    public final void sendMessage(String msg) {
        if(members.isEmpty()) return;

        msg = format(msg);

        System.out.print(ChatColor.stripColor(msg));

        for(String n : members) {
            Player p = Bukkit.getPlayer(n);
            if(isMember(p)) // Check "is member" in case of an override
                p.sendMessage(msg);
        }
    }
    public void sendJoinMessage(Player player) {
        sendMessage(" "+ChatColor.YELLOW+ChatColor.stripColor(player.getDisplayName())+" has joined.");
    }
    public void sendLeaveMessage(Player player) {
        sendMessage(" "+ChatColor.YELLOW+ChatColor.stripColor(player.getDisplayName())+" has left.");
    }

    // Channel Formatting
    public final String format(String old) {
        return getDisplayName()+(old.equals("<%1$s> %2$s")?" ":"")+old;
    }
    public String getDisplayName() {
        return tag==null?"["+name+"]":(tag.replaceAll("(?i)`(?=[0-9A-F])", "\u00A7")+ChatColor.WHITE);
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
        if(alias == s) return true;

        if(s != null && getManager().getChannel(s) != null) return false;

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
        if(owner == null || owner.length() == 0) return false;
        return owner.equalsIgnoreCase(player.getName());
    }

    // Member Functions - For use with /cc who
    public Set<String> getMembers(Player player) {
        return new HashSet<String>(members);
    }
    // If member functions
    public Boolean isSenderMember(Player player) {
        return /*player.hasPermission("ChanChat.admin") ||*/ isMember(player);
    }
    public Boolean isMember(Player player) {
        if(player == null) return false;
        return isMember(player.getName());
    }
    private Boolean isMember(String player) {
        return members.contains(player);
    }
    // Add members
    public final void addMember(Player player) {
        addMember(player, false);
    }
    public final void addMember(Player player, Boolean alert) {
        if(player == null) return;

        addMember(player.getName());

        if(alert) sendJoinMessage(player);
    }
    private void addMember(String player) {
        if(player == null) return;

        members.add(player);
    }
    // Remove members
    public final void delMember(Player player) {
        delMember(player, false);
    }
    public final void delMember(Player player, Boolean alert) {
        if(player == null || !isMember(player)) return;

        if(alert) sendLeaveMessage(player);

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
    public void handleEvent(PlayerChatEvent event) {
        if(isSenderMember(event.getPlayer())) {
            for(Player p : new HashSet<Player>(event.getRecipients()))
                if(!isMember(p))
                    event.getRecipients().remove(p);
            // !!! I want to format outside of the handleEvent...
            event.setFormat(format(event.getFormat()));
        } else {
            event.getPlayer().sendMessage(format(" Not a member"));
            event.setCancelled(true);
        }
    }
    // Per-Channel "can join" checks. ^^
    // !!! Make a "JoinChannel" Event
    public boolean canJoin(Player player) {
        return false;
    }

    // Custom Channel Methods... BAD !!!
    public void callSave() {} // !!! Save Event
    public void callReload() {} /// !!! Reload Event... Use /cc reload <module> ?

    // Lovely Booleans
    public final Boolean isSaved() {
        return !type.equals(Type.Private) && !type.equals(Type.Custom);
    }

    // This may or may not be pushed.
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("name", getName());
        map.put("alias", getAlias());
        map.put("tag", getTag());
        map.put("auto", isAuto());
        map.put("listed", isListed());
        map.put("owner", getOwner());
        map.put("pass", getPass());

        return map;
    }

    public static Channel deserialize(Map<String, Object> values) {
        Channel chan = new Channel((String) values.get("name"), Type.Global);

        chan.setAlias((String) values.get("alias"));
        chan.setTag((String) values.get("tag"));
        chan.setOwner((String) values.get("owner"));
        chan.setPass((String) values.get("pass"));
        chan.setAuto((Boolean) values.get("auto"));
        chan.setListed((Boolean) values.get("listed"));

        return chan;
    }
}
