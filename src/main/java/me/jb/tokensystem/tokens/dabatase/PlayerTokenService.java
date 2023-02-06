package me.jb.tokensystem.tokens.dabatase;

import me.jb.tokensystem.tokens.PlayerTokenData;
import me.jb.tokensystem.utils.async.AsyncCallback;
import org.bukkit.entity.Player;

import java.util.Optional;

public interface PlayerTokenService {

    void loadPlayerTokenData(Player player, AsyncCallback<Boolean> callback);

    void loadOnlinePlayerTokenData(AsyncCallback<Boolean> callback);

    void unloadPlayerTokenData(Player player, AsyncCallback<Boolean> callback);

    void checkPlayer(String player, AsyncCallback<Boolean> callback);

    void getPlayerTokenData(String playerName, AsyncCallback<Optional<PlayerTokenData>> callback);

    void addTokens(String playerName, int addedPoints, AsyncCallback<Boolean> callback);

    void removeTokens(String playerName, int removedPoints, AsyncCallback<Boolean> callback);

    void setTokens(String playerName, int points, AsyncCallback<Boolean> callback);

    void updatePlayer(Player player, AsyncCallback<Boolean> callback);

    void reload();

}
