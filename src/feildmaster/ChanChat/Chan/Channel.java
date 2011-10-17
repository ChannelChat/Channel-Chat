package feildmaster.ChanChat.Chan;

import feildmaster.ChanChat.Util.ChatUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

// TODO: FactionChannel, WorldChannel, LocalChannel, TownyChannel
// TODO: AutoJoin on ChannelChange...
// TODO: Channel Joining based on Permissions? Channel Permissions?
public class Channel {
    private final String name;
    private final Type type;
    private String tag = null;
    private String owner = null;
    private String pass = null;
    private Boolean auto_join = false;
    private Boolean listed = false;
    private Set<String> members = new HashSet<String>();

    public enum Type {
        Faction,
        Global,
        Local,
        Private,
        World;

        static final List<String> list = new ArrayList<String>();

        public static boolean contains(String name) {
            return list.contains(name);
        }

        public static Type betterValueOf(String name) {
            if(name == null) return Type.Global;

            for(Type t : values())
                if(t.toString().equals(name))
                    return t;

            return Type.Global;
        }

        static {
            for(Type t : values())
                list.add(t.name());
        }
    }

    public Channel(Channel c, Type t) {
        name = c.name;
        type = t;
        tag = c.tag;
        owner = c.owner;
        pass = c.pass;
        auto_join = c.auto_join;
        listed = c.listed;
        members = c.members;
    }

    public Channel(String n, Type t) {
        name = n;
        type = t;
    }
    public Channel(String n, Player player) {
        name = n;
        type = Type.Private;
        owner = player.getName();
        members.add(player.getName());
    }

    public String format(String old) {
        return getDisplayName()+(old.equals("<%1$s> %2$s")?" ":"")+old;
    }

    public void sendMessage(String msg) {
        for(String n : getMembers())
            ChatUtil.getPlayer(n).sendMessage(format(msg));
    }

    //
    protected String getDisplayName() {
        return tag==null?"["+name+"]":(tag.replaceAll("(?i)`(?=[0-F])", "\u00A7")+ChatColor.WHITE);
    }

    // Channel Name Functions
    public String getName() {
        return name;
    }

    // Channel Type
    public Type getType() {
        return type;
    }

    // Channel Tag
    public void setTag(String t) {
        tag = t;
    }
    public String getTag() {
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
        if(!isMember(name))
            addMember(name, false);
    }

    // Member Functions
    public Set<String> getMembers() {
        return members;
    }
    public Boolean isMember(Player player) {
        return isMember(player.getName());
    }
    public Boolean isMember(String player) {
        return getMembers().contains(player);
    }
    public void addMember(Player player) {
        addMember(player.getName(), false);
    }
    public void addMember(Player player, Boolean alert) {
        addMember(player.getName(), alert);
    }
    private void addMember(String player, Boolean alert) {
        getMembers().add(player);
        if(alert) sendMessage(" "+player+" has joined.");
    }
    public void delMember(Player player) {
        delMember(player.getName(), false);
    }
    public void delMember(Player player, Boolean alert) {
        delMember(player.getName(), alert);
    }
    private void delMember(String player, Boolean alert) {
        if(alert) sendMessage(" "+player+" has left.");
        getMembers().remove(player);
    }

    // Password Functions
    public void removePass() { // Not used. :o
        setPass(null);
    }
    public String getPass() {
        return pass;
    }
    public void setPass(String p) {
        pass = p;
    }

    // Listed functions
    public void setListed(Boolean l) {
        listed = l;
    }

    // Auto functions
    public void setAuto(Boolean v) {
        auto_join = v;
    }

    public void handleEvent(PlayerChatEvent event) {
        if(isMember(event.getPlayer())) {
            for(Player p : new HashSet<Player>(event.getRecipients()))
                if(!isMember(p))
                    event.getRecipients().remove(p);
            event.setFormat(format(event.getFormat()));
        } else {
            event.getPlayer().sendMessage(format("Not a member"));
            event.setCancelled(true);
            // Shouldn't happen, but I'll add an error here later.
        }
    }

    // Lovely Booleans
    public Boolean isOwner(Player player) {
        if(owner == null || owner.isEmpty()) return false;
        return owner.equalsIgnoreCase(player.getName());
    }
    public Boolean isAuto() {
        return auto_join;
    }
    public Boolean isListed() {
        return listed;
    }
    public Boolean isSaved() {
        return !type.equals(Type.Private);
    }

    public FactionChannel toFaction() {
        return (FactionChannel)this;
    }
    public LocalChannel toLocal() {
        return (LocalChannel)this;
    }
    public WorldChannel toWorld() {
        return (WorldChannel)this;
    }

}
