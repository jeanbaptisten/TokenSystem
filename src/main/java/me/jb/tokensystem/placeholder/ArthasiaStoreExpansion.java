package me.jb.tokensystem.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.jb.tokensystem.TokenSystem;
import me.jb.tokensystem.tokens.PlayerTokenData;
import me.jb.tokensystem.tokens.dabatase.PlayerTokenModel;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ArthasiaStoreExpansion extends PlaceholderExpansion {

    private final TokenSystem tokenSystem;
    private final PlayerTokenModel playerTokenModel;

    public ArthasiaStoreExpansion(@NotNull TokenSystem tokenSystem, PlayerTokenModel playerTokenModel) {
        this.tokenSystem = tokenSystem;
        this.playerTokenModel = playerTokenModel;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "arthasiastore";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Niz";
    }

    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {

        if (!params.equals("token")) {
            return null;
        }

        if (player.getPlayer() == null)
            return "UNKNOWN";

        Optional<PlayerTokenData> playerTokenDataOptional = this.playerTokenModel.getPlayerTokenData(player.getPlayer());

        return playerTokenDataOptional.map(playerTokenData -> String.valueOf(playerTokenData.getTokens())).orElse("UNKNOWN");

    }
}

