package feildmaster.ChanChat.Commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public interface ChatInterface extends CommandExecutor {
    public void invalidCommand(CommandSender sender, String name);
}
