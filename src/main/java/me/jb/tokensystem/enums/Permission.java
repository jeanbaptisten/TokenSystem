package me.jb.tokensystem.enums;

import org.jetbrains.annotations.NotNull;

public enum Permission {

    SUB_USE,

    USE,
    ADD,
    REMOVE,
    SET,
    GET,

    RELOAD,
    ;

    private final String permission;

    Permission() {
        this.permission = this.name().toLowerCase().replace("_", ".");
    }

    @NotNull
    public String getPermission() {
        return "tokensystem." + this.permission;
    }
}