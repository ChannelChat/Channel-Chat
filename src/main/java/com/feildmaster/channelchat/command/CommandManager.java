//package com.feildmaster.channelchat.command;
//
//import java.util.*;
//import org.bukkit.plugin.Plugin;
//
///**
// * Manages commands and registration
// */
//public class CommandManager {
//    private Map<String, ChannelCommand> registry = new HashMap<String, ChannelCommand>();
//    //private Map<String, List<ChannelCommand>> pRegistry = new HashMap<String, List<ChannelCommand>>();
//
//    // Change this to String command... Other stuff? Maybe?
//    public boolean register(ChannelCommand command) {
////        if (getSet(command.getName()).add(command)) {
////            getPluginSet(command.plugin).add(command);
////            return true;
////        }
//        return false;
//    }
//
//    public ChannelCommand getCommand(String command) {
//        return getSet(command).get(0);
//    }
//
//    private List<ChannelCommand> getPluginSet(Plugin plugin) {
//        String key = plugin.getDescription().getName();
//        if (!pRegistry.containsKey(key)) {
//            pRegistry.put(key, new LinkedList<ChannelCommand>());
//        }
//        return pRegistry.get(key);
//    }
//
//    private synchronized ChannelCommand getSet(String key) {
//        return registry.get(key.toLowerCase());
//    }
//
//    public boolean unregister(ChannelCommand command) {
//        // Unregister
//        return false;
//    }
//
//    public void unregister(Plugin plugin) {
//        // Plugin
//    }
//}
