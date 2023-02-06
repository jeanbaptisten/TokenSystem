package me.jb.tokensystem.listener;

import com.sun.istack.internal.NotNull;
import me.jb.tokensystem.tokens.dabatase.PlayerTokenService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    private final PlayerTokenService playerTokenService;

    public PlayerConnectionListener(@NotNull PlayerTokenService playerTokenService){
        this.playerTokenService = playerTokenService;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        this.playerTokenService.loadPlayerTokenData(event.getPlayer(), boolCallback -> {
            if(!boolCallback)
                event.getPlayer().kickPlayer("Error while getting player information. Please, contact an administrator !");
        });
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        this.playerTokenService.unloadPlayerTokenData(event.getPlayer(),  boolCallback -> {});
    }

}
