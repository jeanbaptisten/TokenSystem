package me.jb.tokensystem.tokens.dabatase;

import me.jb.tokensystem.tokens.PlayerTokenData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;

public interface PlayerTokenModel {

    // List PlayerTokenData

    void addPlayerTokenData(PlayerTokenData tokenData);

    void replacePlayerTokenData(PlayerTokenData tokenData);

    void removePlayerTokenData(Player player);

    void clearCache();

    HashMap<String, PlayerTokenData> getPlayerTokenDataMap();

    boolean containsPlayer(Player player);

    boolean containsPlayer(String player);

    boolean containsPlayerTokenData(PlayerTokenData player);

    Optional<PlayerTokenData> getPlayerTokenData(Player player);

    Optional<PlayerTokenData> getPlayerTokenData(String player);
}
