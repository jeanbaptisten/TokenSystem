package me.jb.tokensystem.tokens.dabatase.impl;

import me.jb.tokensystem.tokens.PlayerTokenData;
import me.jb.tokensystem.tokens.dabatase.PlayerTokenModel;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;

public class CraftPlayerTokenModel implements PlayerTokenModel {

    private final HashMap<String, PlayerTokenData> playerTokenDataMap = new HashMap<>();

    @Override
    public void addPlayerTokenData(PlayerTokenData tokenData) {
        if (this.playerTokenDataMap.containsKey(tokenData.getPlayerName()))
            throw new IllegalArgumentException(String.format("Token data already exist for player \"%s\".", tokenData.getPlayerName()));

        this.playerTokenDataMap.put(tokenData.getPlayerName(), tokenData);
    }

    @Override
    public void replacePlayerTokenData(PlayerTokenData tokenData) {
        this.playerTokenDataMap.put(tokenData.getPlayerName(), tokenData);
    }

    @Override
    public void removePlayerTokenData(Player player) {
        Optional<PlayerTokenData> playerTokenDataOptional = this.getPlayerTokenData(player);

        if (!playerTokenDataOptional.isPresent())
            throw new IllegalArgumentException(String.format("Token data doesn't exist for player \"%s\".", player.getName()));

        PlayerTokenData playerTokenData = playerTokenDataOptional.get();

        this.playerTokenDataMap.remove(playerTokenData.getPlayerName());
    }

    @Override
    public void clearCache() {
        this.playerTokenDataMap.clear();
    }

    public HashMap<String, PlayerTokenData> getPlayerTokenDataMap(){
        return this.playerTokenDataMap;
    }

    @Override
    public boolean containsPlayer(Player player) {
        return this.playerTokenDataMap.containsKey(player.getName());
    }

    @Override
    public boolean containsPlayer(String player) {
        return this.playerTokenDataMap.containsKey(player);
    }

    @Override
    public boolean containsPlayerTokenData(PlayerTokenData tokenData) {
        return this.playerTokenDataMap.containsValue(tokenData);
    }

    @Override
    public Optional<PlayerTokenData> getPlayerTokenData(Player player) {
        return Optional.ofNullable(this.playerTokenDataMap.get(player.getName()));
    }

    @Override
    public Optional<PlayerTokenData> getPlayerTokenData(String player) {
        return Optional.ofNullable(this.playerTokenDataMap.get(player));
    }
}
