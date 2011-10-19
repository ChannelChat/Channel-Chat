/*
 * Copyright (c) 2011. SwearWord
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * No modifications may be made to the Software. User must use the code as is.
 *
 * Users of any software implementing this Software must be made aware of this Software's
 * implementation. This Software may not be executed on uninformed clients.
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.blockface.bukkitstats;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.net.URL;

public class CallHome{

    private static Configuration cfg=null;

    public static void load(Plugin plugin) {
        if(cfg==null) if(!verifyConfig()) return;

        if(cfg.getBoolean("opt-out",false)) return;

        plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin,new CallTask(plugin,cfg.getBoolean("list-server",true)),0L,20L*60L*60);
        System.out.println(plugin.getDescription().getName() + " is keeping usage stats. To opt-out for whatever bizarre reason, check plugins/stats.");
    }

    private static Boolean verifyConfig() {
        File config = new File("plugins/stats/config.yml");

        if(!config.exists())
            System.out.println("BukkitStats is initializing for the first time. To opt-out check plugins/stats");

        cfg=new Configuration(config);
        cfg.load();
        cfg.getBoolean("opt-out",false);
        cfg.getBoolean("list-server", true);
        cfg.save();

        if(!config.exists()) {
            System.out.println("BukkitStats failed to save configuration.");
            return false;
        }

        return true;
    }
}

class CallTask implements Runnable {
    private Plugin plugin;
    private int pub=1;

    public CallTask(Plugin plugin,Boolean pub) {
        this.plugin = plugin;
        if(!pub) this.pub = 0;
    }

    public void run() {
        try {
            postUrl();
        } catch (Exception ignored) {
            System.out.println("Could not call home.");
        }
    }

    private void postUrl() throws Exception {
        String url = String.format("http://usage.blockface.org/update.php?name=%s&build=%s&plugin=%s&port=%s&public=%s&bukkit=%s",
                plugin.getServer().getName(),
                plugin.getDescription().getVersion(),
                plugin.getDescription().getName(),
                plugin.getServer().getPort(),
                pub,
                Bukkit.getVersion());

        new URL(url).openConnection();
    }
}
