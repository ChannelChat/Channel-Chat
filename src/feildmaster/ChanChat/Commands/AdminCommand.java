package feildmaster.ChanChat.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class AdminCommand extends BaseCommands {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        /* Commands:
         * - Ban
         * - Unban
         * - password
         * - adding ability
         * - tag
         * - mute (join, no talking)
         * Future:
         * - mods?
         */


        return true;
    }

    public void invalidCommand(CommandSender sender, String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
