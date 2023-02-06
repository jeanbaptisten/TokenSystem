package me.jb.tokensystem.tokens.dabatase.impl;

import com.sun.istack.internal.NotNull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import me.jb.tokensystem.exceptions.PlayerTokenException;
import me.jb.tokensystem.sql.DbConnection;
import me.jb.tokensystem.tokens.PlayerTokenData;
import me.jb.tokensystem.tokens.dabatase.PlayerTokenDAO;
import org.bukkit.entity.Player;

public class SQLPlayerTokenDAO implements PlayerTokenDAO {

  private final DbConnection dbConnection;

  public SQLPlayerTokenDAO(@NotNull DbConnection dbConnection) {
    if (dbConnection == null)
      throw new IllegalArgumentException("Database connection cannot be null");
    this.dbConnection = dbConnection;

    this.initTables();
  }

  @Override
  public Optional<PlayerTokenData> getPlayerTokenData(Player player) throws PlayerTokenException {
    String stringSQL = "SELECT * FROM tokens WHERE player_uuid=?;";

    Connection connection = dbConnection.getConnection();

    try (PreparedStatement preparedStatement = connection.prepareStatement(stringSQL)) {
      preparedStatement.setString(1, player.getUniqueId().toString());

      ResultSet resultSet = preparedStatement.executeQuery();

      if (resultSet.next()) {
        UUID playerUUID = player.getUniqueId();
        String playerName = player.getName();
        int playerTokens = resultSet.getInt("player_tokens");
        return Optional.of(new PlayerTokenData(playerUUID, playerName, playerTokens));
      }

      return Optional.empty();
    } catch (SQLException troubles) {
      throw new PlayerTokenException(
          String.format("Cannot get player token data in database : %s", player.getName()),
          troubles);
    }
  }

  @Override
  public Optional<PlayerTokenData> getPlayerTokenData(String playerName)
      throws PlayerTokenException {
    String stringSQL = "SELECT * FROM tokens WHERE player_name=?;";

    Connection connection = dbConnection.getConnection();

    try (PreparedStatement preparedStatement = connection.prepareStatement(stringSQL)) {
      preparedStatement.setString(1, playerName);

      ResultSet resultSet = preparedStatement.executeQuery();

      if (resultSet.next()) {
        UUID playerUUID = UUID.fromString(resultSet.getString("player_uuid"));
        int playerTokens = resultSet.getInt("player_tokens");
        return Optional.of(new PlayerTokenData(playerUUID, playerName, playerTokens));
      }

      return Optional.empty();
    } catch (SQLException troubles) {
      throw new PlayerTokenException(
          String.format("Cannot get player token data in database : %s", playerName), troubles);
    }
  }

  @Override
  public void createPlayerTokenData(PlayerTokenData tokenData) throws PlayerTokenException {
    String stringSQL =
        "INSERT INTO tokens (player_uuid, player_name, player_tokens)" + "VALUES (?,?,?);";

    Connection connection = dbConnection.getConnection();

    try (PreparedStatement preparedStatement = connection.prepareStatement(stringSQL)) {
      preparedStatement.setString(1, tokenData.getPlayerUUID().toString());
      preparedStatement.setString(2, tokenData.getPlayerName());
      preparedStatement.setInt(3, tokenData.getTokens());

      preparedStatement.execute();
    } catch (SQLException troubles) {
      throw new PlayerTokenException(
          String.format("Create player token data in database : %s", tokenData.getPlayerName()),
          troubles);
    }
  }

  @Override
  public boolean containsPlayer(Player player) throws PlayerTokenException {
    String stringSQL = "SELECT * FROM tokens WHERE player_uuid=?;";

    Connection connection = dbConnection.getConnection();

    try (PreparedStatement preparedStatement = connection.prepareStatement(stringSQL)) {
      preparedStatement.setString(1, player.getUniqueId().toString());

      ResultSet resultSet = preparedStatement.executeQuery();

      return resultSet.next();

    } catch (SQLException troubles) {
      throw new PlayerTokenException(
          String.format("Cannot get player token data in database : %s", player.getName()),
          troubles);
    }
  }

  @Override
  public boolean containsPlayer(String playerName) throws PlayerTokenException {
    String stringSQL = "SELECT COUNT(1) FROM tokens WHERE player_name=?;";

    Connection connection = dbConnection.getConnection();

    try (PreparedStatement preparedStatement = connection.prepareStatement(stringSQL)) {

      preparedStatement.setString(1, playerName);

      ResultSet resultSet = preparedStatement.executeQuery();

      return resultSet.getInt(1) == 1;

    } catch (SQLException troubles) {
      throw new PlayerTokenException(
          String.format("Cannot get player token data in database : %s", playerName), troubles);
    }
  }

  @Override
  public void updateTokenData(PlayerTokenData playerTokenData) throws PlayerTokenException {
    String stringSQL = "UPDATE tokens SET player_tokens=? WHERE player_uuid=?;";

    Connection connection = dbConnection.getConnection();

    try (PreparedStatement preparedStatement = connection.prepareStatement(stringSQL)) {
      preparedStatement.setString(1, String.valueOf(playerTokenData.getTokens()));
      preparedStatement.setString(2, playerTokenData.getPlayerUUID().toString());
      preparedStatement.execute();
    } catch (SQLException troubles) {
      throw new PlayerTokenException(
          String.format(
              "Create player token data in database : %s", playerTokenData.getPlayerName()),
          troubles);
    }
  }

  @Override
  public void updateNickname(Player player) throws PlayerTokenException {
    String stringSQL = "UPDATE tokens " + "SET player_name=? " + "WHERE player_uuid=?;";

    Connection connection = dbConnection.getConnection();

    try (PreparedStatement preparedStatement = connection.prepareStatement(stringSQL)) {
      preparedStatement.setString(1, player.getName());
      preparedStatement.setString(2, player.getUniqueId().toString());

      preparedStatement.execute();
    } catch (SQLException troubles) {
      throw new PlayerTokenException(
          String.format("Create player token data in database : %s", player.getName()), troubles);
    }
  }

  private void initTables() {
    String sqlQueryPlayers =
        "CREATE TABLE IF NOT EXISTS tokens "
            + "("
            + "player_uuid CHAR(36) NOT NULL,"
            + "player_name VARCHAR(16) NOT NULL,"
            + "player_tokens INT(255) NOT NULL,"
            + "PRIMARY KEY (player_uuid, player_name)"
            + ");";

    Connection connection = this.dbConnection.getConnection();

    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQueryPlayers)) {
      preparedStatement.execute();
    } catch (SQLException troubles) {
      troubles.printStackTrace();
    }
  }
}
