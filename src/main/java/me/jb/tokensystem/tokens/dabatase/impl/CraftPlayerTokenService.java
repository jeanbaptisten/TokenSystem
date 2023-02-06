package me.jb.tokensystem.tokens.dabatase.impl;

import com.sun.istack.internal.NotNull;

import java.util.Optional;
import java.util.logging.Level;

import me.jb.tokensystem.TokenSystem;
import me.jb.tokensystem.exceptions.PlayerTokenException;
import me.jb.tokensystem.tokens.PlayerTokenData;
import me.jb.tokensystem.tokens.dabatase.PlayerTokenDAO;
import me.jb.tokensystem.tokens.dabatase.PlayerTokenModel;
import me.jb.tokensystem.tokens.dabatase.PlayerTokenService;
import me.jb.tokensystem.utils.async.AsyncCallback;
import me.jb.tokensystem.utils.async.AsyncRequest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CraftPlayerTokenService implements PlayerTokenService {

    private final PlayerTokenDAO playerTokenDAO;
    private final PlayerTokenModel playerTokenModel;
    private final TokenSystem tokenSystem;

    public CraftPlayerTokenService(
            TokenSystem tokenSystem,
            PlayerTokenDAO playerTokenDAO,
            PlayerTokenModel playerTokenModel) {
        this.tokenSystem = tokenSystem;
        this.playerTokenDAO = playerTokenDAO;
        this.playerTokenModel = playerTokenModel;
    }

    @Override
    public void loadPlayerTokenData(Player player, AsyncCallback<Boolean> callback) {
        new AsyncRequest(this.tokenSystem).executeTask(() -> this.loadPlayer(player, callback));
    }

    @Override
    public void loadOnlinePlayerTokenData(AsyncCallback<Boolean> callback) {
        new AsyncRequest(this.tokenSystem)
                .executeTask(
                        () -> Bukkit.getOnlinePlayers().forEach(player -> this.loadPlayer(player, callback)));
    }

    @Override
    public void unloadPlayerTokenData(Player player, AsyncCallback<Boolean> callback) {
        try {
            this.playerTokenModel.removePlayerTokenData(player);
            callback.onResponse(true);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onResponse(false);
        }
    }

    @Override
    public void checkPlayer(String playerName, AsyncCallback<Boolean> callback) {
        if (this.playerTokenModel.containsPlayer(playerName)) {
            callback.onResponse(true);
            return;
        }

        new AsyncRequest(this.tokenSystem)
                .executeTask(
                        () -> {
                            try {
                                if (this.playerTokenDAO.containsPlayer(playerName))
                                    this.runSync(() -> callback.onResponse(true));
                                else this.runSync(() -> callback.onResponse(false));
                            } catch (PlayerTokenException e) {
                                this.runSync(() -> callback.onResponse(false));
                                e.printStackTrace();
                            }
                        });
    }

    @Override
    public void getPlayerTokenData(
            String playerName, AsyncCallback<Optional<PlayerTokenData>> callback) {

        // Check if player is in cache,
        // So I don't have to do async request. (time saving)
        // Then check in db.

        Optional<PlayerTokenData> modelPlayerTokenDataOptional =
                this.playerTokenModel.getPlayerTokenData(playerName);

        if (modelPlayerTokenDataOptional.isPresent()) {
            callback.onResponse(modelPlayerTokenDataOptional);
            return;
        }

        new AsyncRequest(this.tokenSystem)
                .executeTask(
                        () -> {
                            try {
                                Optional<PlayerTokenData> dbPlayerTokenDataOptional =
                                        this.playerTokenDAO.getPlayerTokenData(playerName);
                                this.runSync(() -> callback.onResponse(dbPlayerTokenDataOptional));
                            } catch (PlayerTokenException e) {
                                e.printStackTrace();
                            }
                        });
    }

    @Override
    public void addTokens(String playerName, int addedPoints, AsyncCallback<Boolean> callback) {
    new AsyncRequest(this.tokenSystem)
        .executeTask(
            () -> {
              Optional<PlayerTokenData> playerTokenDataOptional =
                  this.playerTokenModel.getPlayerTokenData(playerName);

              // If player is connected, add them from cache
              if (playerTokenDataOptional.isPresent()) {
                PlayerTokenData playerTokenData = playerTokenDataOptional.get();
                playerTokenData.addTokens(addedPoints);
                this.updateTokenData(callback, playerTokenData);
                return;
              }

              // Add them in database
              try {
                Optional<PlayerTokenData> optionalPlayerTokenData =
                    this.playerTokenDAO.getPlayerTokenData(playerName);

                if (!optionalPlayerTokenData.isPresent()) {
                  this.runSync(() -> callback.onResponse(false));
                  return;
                }

                PlayerTokenData tokenData = optionalPlayerTokenData.get();
                tokenData.addTokens(addedPoints);

                this.playerTokenDAO.updateTokenData(tokenData);

                this.runSync(() -> callback.onResponse(true));
              } catch (PlayerTokenException e) {
                this.runSync(() -> callback.onResponse(false));
                e.printStackTrace();
              }
            });
    }

    @Override
    public void removeTokens(String playerName, int removedPoints, AsyncCallback<Boolean> callback) {
        new AsyncRequest(this.tokenSystem)
                .executeTask(
                        () -> {
                            Optional<PlayerTokenData> playerTokenDataOptional =
                                    this.playerTokenModel.getPlayerTokenData(playerName);

                            // If player is connected, remove them from cache
                            if (playerTokenDataOptional.isPresent()) {
                                PlayerTokenData playerTokenData = playerTokenDataOptional.get();
                                playerTokenData.removeTokens(removedPoints);
                                this.updateTokenData(callback, playerTokenData);
                                return;
                            }

                            // Remove them from database
                            try {
                                Optional<PlayerTokenData> optionalPlayerTokenData =
                                        this.playerTokenDAO.getPlayerTokenData(playerName);
                                if (!optionalPlayerTokenData.isPresent()) {
                                    this.runSync(() -> callback.onResponse(false));
                                }

                                PlayerTokenData tokenData = optionalPlayerTokenData.get();
                                tokenData.removeTokens(removedPoints);

                                this.playerTokenDAO.updateTokenData(tokenData);

                                this.runSync(() -> callback.onResponse(true));

                            } catch (PlayerTokenException e) {
                                this.runSync(() -> callback.onResponse(false));
                                e.printStackTrace();
                            }
                        });
    }

    @Override
    public void setTokens(String playerName, int points, AsyncCallback<Boolean> callback) {

        Optional<PlayerTokenData> modelPlayerTokenDataOptional =
                this.playerTokenModel.getPlayerTokenData(playerName);

        if (modelPlayerTokenDataOptional.isPresent()) {
            PlayerTokenData playerTokenData = modelPlayerTokenDataOptional.get();
            playerTokenData.setTokens(points);
            this.playerTokenModel.replacePlayerTokenData(playerTokenData);
        }

        new AsyncRequest(this.tokenSystem)
                .executeTask(
                        () -> {
                            try {
                                Optional<PlayerTokenData> dbPlayerTokenDataOptional =
                                        this.playerTokenDAO.getPlayerTokenData(playerName);

                                if (dbPlayerTokenDataOptional.isPresent()) {

                                    PlayerTokenData dbPlayerTokenData = dbPlayerTokenDataOptional.get();
                                    dbPlayerTokenData.setTokens(points);

                                    this.playerTokenDAO.updateTokenData(dbPlayerTokenData);
                                    callback.onResponse(true);
                                } else callback.onResponse(false);
                            } catch (PlayerTokenException e) {
                                callback.onResponse(false);
                                e.printStackTrace();
                            }
                        });
    }

    @Override
    public void updatePlayer(Player player, AsyncCallback<Boolean> callback) {
        new AsyncRequest(this.tokenSystem)
                .executeTask(
                        () -> {
                            try {
                                this.playerTokenDAO.updateNickname(player);
                                this.runSync(() -> callback.onResponse(true));
                            } catch (PlayerTokenException e) {
                                this.runSync(() -> callback.onResponse(false));
                                e.printStackTrace();
                            }
                        });
    }

    @Override
    public void reload() {
        this.playerTokenModel.clearCache();
        this.loadOnlinePlayerTokenData(
                boolCallback -> {
                    if (!boolCallback)
                        System.out.println("[ArthasiaStore] Error while loading online player data !");
                });
    }

    private void runSync(@NotNull Runnable runnable) {
        Bukkit.getScheduler().runTask(this.tokenSystem, runnable);
    }

    private void loadPlayer(Player player, AsyncCallback<Boolean> callback) {
        try {
            Optional<PlayerTokenData> optionalPlayerTokenData =
                    this.playerTokenDAO.getPlayerTokenData(player);
            PlayerTokenData playerTokenData;
            // playerTokenData = optionalPlayerTokenData.orElseGet(() -> new PlayerTokenData(player));

            if (!optionalPlayerTokenData.isPresent()) {
                playerTokenData = new PlayerTokenData(player.getUniqueId(), player.getName());
                this.playerTokenDAO.createPlayerTokenData(playerTokenData);
            } else {
                playerTokenData = optionalPlayerTokenData.get();
            }

            if (!this.playerTokenModel.containsPlayer(player)) {
                this.playerTokenModel.addPlayerTokenData(playerTokenData);
                this.runSync(() -> callback.onResponse(true));
            } else {
                this.tokenSystem
                        .getLogger()
                        .log(Level.SEVERE, "Error while loading player " + player.getName() + " !");
                this.runSync(() -> callback.onResponse(false));
            }

        } catch (PlayerTokenException e) {
            this.runSync(() -> callback.onResponse(false));
            e.printStackTrace();
        }
    }

    private void updateTokenData(AsyncCallback<Boolean> callback, PlayerTokenData playerTokenData) {
        this.playerTokenModel.replacePlayerTokenData(playerTokenData);

        try {
            this.playerTokenDAO.updateTokenData(playerTokenData);
            this.runSync(() -> callback.onResponse(true));
        } catch (PlayerTokenException e) {
            this.runSync(() -> callback.onResponse(false));
            e.printStackTrace();
        }
    }
}
