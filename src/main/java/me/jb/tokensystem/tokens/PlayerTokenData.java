package me.jb.tokensystem.tokens;

import com.sun.istack.internal.NotNull;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerTokenData {

  private final ReentrantLock reentrantLock = new ReentrantLock();
  private final UUID playerUUID;
  private final String playerName;
  private int tokens;

  public PlayerTokenData(@NotNull UUID playerUUID, String playerName, int tokens) {
    if (playerName.trim().equals(""))
      throw new IllegalArgumentException("Tokens cannot be negative.");

    this.playerUUID = playerUUID;
    this.playerName = playerName;
    this.setTokens(tokens);
  }

  public PlayerTokenData(@NotNull UUID playerUUID, String playerName) {
    this(playerUUID, playerName, 0);
  }

  public Optional<Player> getPlayer() {
    return Optional.ofNullable(Bukkit.getPlayer(this.playerUUID));
  }

  public String getPlayerName() {
    return this.playerName;
  }

  public UUID getPlayerUUID() {
    return this.playerUUID;
  }

  public synchronized int getTokens() {
    return this.tokens;
  }

  public synchronized void setTokens(int tokens) {
    if (tokens < 0) throw new IllegalArgumentException("Tokens cannot be negative.");

    this.tokens = tokens;
  }

  public synchronized void removeTokens(int removedTokens) {
    this.reentrantLock.lock();
    if (this.tokens - removedTokens <= 0) this.tokens = 0;
    else this.tokens -= removedTokens;
    this.reentrantLock.unlock();
  }

  public synchronized void addTokens(int addedTokens) {
    this.reentrantLock.lock();
    this.tokens += addedTokens;
    this.reentrantLock.unlock();
  }
}
