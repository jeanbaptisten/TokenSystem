package me.jb.tokensystem.exceptions;

public class PlayerTokenException extends Exception {

    public PlayerTokenException(String message) {
        super(message);
    }

    public PlayerTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
