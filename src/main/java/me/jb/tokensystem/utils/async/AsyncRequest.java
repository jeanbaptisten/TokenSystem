package me.jb.tokensystem.utils.async;

import com.sun.istack.internal.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class AsyncRequest {

    private final Plugin plugin;

    public AsyncRequest(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    public void executeTask(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, runnable);
    }

}
