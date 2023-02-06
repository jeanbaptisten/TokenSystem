package me.jb.tokensystem.enums.config;

import org.jetbrains.annotations.NotNull;

public enum Message {

    NO_PERM("noPerm"),
    ERROR("helpCmd"),
    NOT_INT("notInteger"),
    NEGATIVE_INT("negativeInteger"),
    UNKNOWN_PLAYER("unknownPlayer"),

    SENDER_NEGATIVE_TOKENS("senderNegativeTokens"),
    PLAYER_NEGATIVE_TOKENS("playerNegativeTokens"),

    ERROR_ADD_TOKEN("errorAddToken"),
    ERROR_REMOVE_TOKEN("errorRemoveToken"),
    ERROR_SET_TOKEN("errorSetToken"),

    PLAYER_TOKEN_ADDED("playerTokenAdded"),
    PLAYER_TOKEN_REMOVED("playerTokenRemoved"),
    PLAYER_TOKEN_SET("playerTokenSet"),

    SUB_NOT_ENOUGH_PLAYERS("subNotEnoughPlayers"),
    SUB_BROADCAST_MESSAGE("subBroadcastMessage"),

    TOKEN_ADDED("tokenAdded"),
    TOKEN_REMOVED("tokenRemoved"),
    TOKEN_GET("tokenGet"),
    TOKEN_SET("tokenSet"),

    RELOAD("reload"),


    ;

    private final String key;

    Message(String key) {
        this.key = key;
    }

    @NotNull
    public String getKey() {
        return this.key;
    }
}
