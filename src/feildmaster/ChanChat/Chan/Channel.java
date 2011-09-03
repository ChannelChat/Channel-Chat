package feildmaster.ChanChat.Chan;

import feildmaster.ChanChat.Util.ChatUtil;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.Player;

public class Channel {
    private String name;
    private String owner;
    private String pass;
    private Boolean saved = false;
    private Boolean auto_join = false;
    private Set<String> members = new HashSet<String>();
    public Channel() {
    }
    public Channel(String n, Boolean a) {
        name = n;
        auto_join = a;
    }
    public Channel(String n, String o, String p, Boolean a) {
        name = n;
        owner = o;
        pass = p;
        auto_join = a;
        saved = true;
    }
    public Channel(String n, Player player) {
        name = n;
        owner = player.getName();
        members.add(player.getName());
    }

    public String format(String old) {
        return "["+name+"] "+old;
    }

    public void sendMessage(String msg) {
        if(!members.isEmpty())
            for(String n : getMembers())
                ChatUtil.getPlayer(n).sendMessage(format(msg));
    }

    // Channel Name Functions
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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
    public Boolean isOwner(Player player) {
        return owner.equalsIgnoreCase(player.getName());
    }

    // Member Functions
    public Set<String> getMembers() {
        return members;
    }
    public Boolean isMember(Player player) {
        return members.contains(player.getName());
    }
    public void addMember(Player player) {
        addMember(player.getName(), false);
    }
    public void addMember(Player player, Boolean alert) {
        addMember(player.getName(), alert);
    }
    private void addMember(String player, Boolean alert) {
        members.add(player);
        if(alert) sendMessage(player+" has joined.");
    }

    public void delMember(Player player) {
        delMember(player.getName(), false);
    }
    public void delMember(Player player, Boolean alert) {
        delMember(player.getName(), alert);
    }
    private void delMember(String player, Boolean alert) {
        if(alert) sendMessage(player+" has left.");
        members.remove(player);
    }

    // Password Functions
    public void removePass() {
        setPass(null);
    }
    public String getPass() {
        return pass;
    }
    public void setPass(String p) {
        pass = p;
    }


    public void setSaved(Boolean p) {
        saved = p;
    }
    public Boolean isSaved() {
        return saved;
    }
    public Boolean isAuto() {
        return auto_join;
    }
}
