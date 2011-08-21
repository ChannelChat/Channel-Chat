package feildmaster.ChanChat.Chan;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Channel {
    private String name;
    private String owner;
    private String pass;
    private Boolean perma;
    private ChatColor color = ChatColor.GRAY;
    private Map<String,Player> members = new HashMap<String,Player>();

    public void sendMessage(String msg) {
        if(!members.isEmpty())
            for(Player player : members.values())
                player.sendMessage(color+"["+name+"] "+msg);
    }

    // Channel Name Functions
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    // Set colors
    public void setColor(String name) {
        color = ChatColor.valueOf(name);
    }
    public void setColor(Integer code) {
        color = ChatColor.getByCode(code);
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
    public Collection<Player> getMembers() {
        return members.values();
    }
    public Boolean isMember(Player player) {
        return members.keySet().contains(player.getName());
    }
    public void addMember(Player player) {
        members.put(player.getName(),player);
    }

    public void delMember(Player player) {
        members.remove(player.getName());
    }
    
    // Password Functions
    public String getPass() {
        return pass;
    }
    public void setPass(String pass) {
        this.pass = pass;
    }
    
    
    public void setPerm(Boolean p) {
        perma = p;
    }
    public Boolean isPerm() {
        return perma;
    }
}
