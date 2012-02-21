package com.feildmaster.channelchat.command;

import java.util.*;
import org.bukkit.plugin.Plugin;

/**
 * Manages commands and registration
 */
public class CommandManager {
    private Map<String, List<ChannelCommand>> registry = new HashMap<String, List<ChannelCommand>>();
    private Map<String, List<ChannelCommand>> pRegistry = new HashMap<String, List<ChannelCommand>>();

    public boolean register(ChannelCommand command) {
        if (getSet(command.getName()).add(command)) {
            getPluginSet(command.plugin).add(command);
            return true;
        }
        return false;
    }

    public ChannelCommand getCommand(String command) {
        return getSet(command).get(0);
    }

    private List<ChannelCommand> getPluginSet(Plugin plugin) {
        String key = plugin.getDescription().getName();
        if (!pRegistry.containsKey(key)) {
            pRegistry.put(key, new LinkedList<ChannelCommand>());
        }
        return pRegistry.get(key);
    }

    private synchronized List<ChannelCommand> getSet(String key) {
        key = key.toLowerCase();
        if (!registry.containsKey(key)) {
            registry.put(key, new LinkedList<ChannelCommand>());
        }
        return registry.get(key);
    }

    public boolean unregister(ChannelCommand command) {
        // Unregister
        return getSet(command.getName()).remove(command);
    }

    public void unregister(Plugin plugin) {
        // Plugin
    }

    private void test() {
        List<ChannelCommand> set = new LinkedList<ChannelCommand>();
        set.add(null); // Add new
        set.add(0, null); // Add new to "first"
        set.get(0); // Get First
    }
}
