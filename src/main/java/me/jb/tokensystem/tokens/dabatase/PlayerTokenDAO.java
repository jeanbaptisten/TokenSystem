package me.jb.tokensystem.tokens.dabatase;

import me.jb.tokensystem.exceptions.PlayerTokenException;
import me.jb.tokensystem.tokens.PlayerTokenData;
import org.bukkit.entity.Player;

import java.util.Optional;

public interface PlayerTokenDAO {

    Optional<PlayerTokenData> getPlayerTokenData(Player player) throws PlayerTokenException;

    Optional<PlayerTokenData> getPlayerTokenData(String playerName) throws PlayerTokenException;

    void createPlayerTokenData(PlayerTokenData tokenData) throws PlayerTokenException;

    boolean containsPlayer(Player player) throws PlayerTokenException;

    boolean containsPlayer(String playerName) throws PlayerTokenException;

    void updateTokenData(PlayerTokenData playerTokenData) throws PlayerTokenException;

    void updateNickname(Player player) throws PlayerTokenException;

}
