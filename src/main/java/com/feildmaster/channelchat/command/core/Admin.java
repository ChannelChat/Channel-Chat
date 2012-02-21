package com.feildmaster.channelchat.command.core;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Admin extends Base {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        /* Commands:
         * - Ban
         * - Unban
         * - password
         * - kick
         * - toggle adding ability
         * - tag
         * - mute (can join, no talking)
         * Future:
         * - mods?
         */

        return true;
    }

    public void invalidCommand(CommandSender sender, String name) {
    }

}
