package com.feildmaster.channelchat.channel;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class WorldChannel extends Channel {
    private World world;

    protected WorldChannel(String name) {
        super(name, Type.World);
    }
    protected WorldChannel(Channel chan) {
        super(chan, Type.World);
    }

    public String getDisplayName() {
        if(getTag() == null) return "["+getName()+"]";

        return super.getDisplayName().replaceAll("(?i)\\{world}", world == null?"":world.getName());
    }

    public void sendJoinMessage(Player player) {
        world = player.getWorld();

        super.sendJoinMessage(player);

        world = null;
    }

    public Boolean isMember(Player player) {
        //if( && world == null) return true;
        if(world == null) return super.isMember(player);
        return super.isMember(player) && world.equals(player.getWorld());
    }

    public void handleEvent(AsyncPlayerChatEvent event) {
        world = event.getPlayer().getWorld();

        super.handleEvent(event);

        world = null;
    }
}
