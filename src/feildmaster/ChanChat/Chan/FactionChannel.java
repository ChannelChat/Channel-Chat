package feildmaster.ChanChat.Chan;

import com.massivecraft.factions.Factions;
import feildmaster.ChanChat.Util.ChatUtil;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

/*
 * Work In Progress
 * TODO: Factions
 */
public class FactionChannel extends Channel {
    private final Factions plugin = ChatUtil.getFactionPlugin();
    private Set<String> members;

    public FactionChannel() {
        super("Faction", Type.Faction);
    }

    public Boolean isMember(Player player) {
        return members.contains(player.getName());
    }

    public void handleEvent(PlayerChatEvent event) {
        members = plugin.getOnlinePlayersInFaction(plugin.getPlayerFactionTag(event.getPlayer()));
        super.handleEvent(event);
    }

    protected String getDisplayName() {
        return "["+getName()+"]"; // Add tag later
    }
}
