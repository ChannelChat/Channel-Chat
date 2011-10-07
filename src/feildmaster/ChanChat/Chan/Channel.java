package feildmaster.ChanChat.Chan;

import feildmaster.ChanChat.Util.ChatUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

// TODO: FactionChannel, WorldChannel, LocalChannel, TownyChannel
// TODO: AutoJoin on ChannelChange...
// TODO: Channel Joining based on Permissions? Channel Permissions?
public class Channel {
    private String name;
    private String tag;
    private String owner;
    private String pass;
    private Type type = Type.Private;
    private int range = 0;
    private int range_squared;
    private Boolean auto_join = false;
    private Boolean listed = false;
    private Set<String> members = new HashSet<String>();

    public enum Type {
        //Faction,
        Global,
        Local,
        Private,
        //Private_Saved,
        World;

        static final List<String> list = new ArrayList<String>();

        public static boolean contains(String name) {
            return list.contains(name);
        }

        static {
            for(Type t : values())
                list.add(t.name());
        }
    }

    public Channel() {}
    public Channel(String n) {
        name = n;
    }
    public Channel(String n, Player player) {
        name = n;
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
    private String getDisplayName() {
        return tag==null||tag.equals("{World}")?"["+name+"]":(tag.replaceAll("(?i)`(?=[0-F])", "\u00A7")+ChatColor.WHITE);
    }

    // Channel Name Functions
    public String getName() {
        return name;
    }
    public void setName(String n) {
        name = n;
    }

    // Channel Type
    public Type getType() {
        if(type == null) return Type.Private;
        return type;
    }
    public String getTypeName() {
        if(type == null) return Type.Private.name();
        return type.name();
    }
    public void setType(String t) {
        if(t == null)
            type = Type.Private;
        else if(Type.contains(t))
            type = Type.valueOf(t);
    }
    public void setType(Type t) {
        if(t == null)
            type = Type.Private;
        else
            type = t;
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
        owner = player.getName();
    }
    public void setOwner(String name) {
        owner = name;
    }

    // Member Functions
    public Set<String> getMembers() {
        return members;
    }
    public Boolean isMember(Player player) {
        return getMembers().contains(player.getName());
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

    // Range functions
    public void setRange(int r) {
        range = r;
        range_squared = (int) Math.pow(r, 2);
    }
    public int getRange() {
        return range;
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
        return type.equals(Type.Global) || type.equals(Type.Local) || type.equals(Type.World);
    }
    public Boolean outOfRange(Location l, Location ll) {
        if(l.equals(ll))
            return false;
        if(l.distanceSquared(ll)>range_squared)
            return true;

        return false;
    }
}
