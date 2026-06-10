package dataaccess;

import chess.ChessGame;
import model.*;
import server.JsonUtils;

import java.sql.*;
import java.util.*;

public class MySQLDataAccess implements DataAccess {

    public MySQLDataAccess() throws DataAccessException {
        DatabaseManager.createDatabase();
        initializeDatabase();
    }

    private void initializeDatabase() throws DataAccessException {
        String[] statements = {
                """
        CREATE TABLE IF NOT EXISTS users (
            username VARCHAR(256) NOT NULL PRIMARY KEY,
            password VARCHAR(256) NOT NULL,
            email    VARCHAR(256) NOT NULL
        )
        """,
                """
        CREATE TABLE IF NOT EXISTS auth (
            authToken VARCHAR(256) NOT NULL PRIMARY KEY,
            username  VARCHAR(256) NOT NULL
        )
        """,
                """
        CREATE TABLE IF NOT EXISTS games (
            gameID        INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
            whiteUsername VARCHAR(256),
            blackUsername VARCHAR(256),
            gameName      VARCHAR(256) NOT NULL,
            game          TEXT         NOT NULL,
            gameOver      BOOLEAN      NOT NULL DEFAULT FALSE
        )
        """
        };

        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : statements) {
                try (var ps = conn.prepareStatement(statement)) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to initialize database", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("DELETE FROM users")) {
                ps.executeUpdate();
            }
            try (var ps = conn.prepareStatement("DELETE FROM auth")) {
                ps.executeUpdate();
            }
            try  (var ps = conn.prepareStatement("DELETE FROM games")) {
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear", e);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            ps.setString(1, user.username());
            ps.setString(2, user.password());  // already hashed by UserService
            ps.setString(3, user.email());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create user", e);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var statement = "SELECT username, password, email FROM users WHERE username = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            ps.setString(1, username);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get user", e);
        }
        return null;
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        var gameJson = JsonUtils.toJson(new ChessGame());
        var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, null);
            ps.setString(2, null);
            ps.setString(3, game.gameName());
            ps.setString(4, gameJson);
            ps.executeUpdate();
            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create game", e);
        }
        return 0;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var statement = "SELECT * FROM games WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            ps.setInt(1, gameID);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            JsonUtils.fromJson(rs.getString("game"), ChessGame.class
                            ), rs.getBoolean("gameOver")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to get game", e);
        }
        return null;
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        var statement = "SELECT * FROM games";
        List<GameData> games = new ArrayList<>();

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement);
             var rs = ps.executeQuery()) {

            while (rs.next()) {
                games.add(new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        JsonUtils.fromJson(rs.getString("game"), ChessGame.class
                        ), rs.getBoolean("gameOver")
                ));
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to list games", e);
        }

        return games;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        var statement = """
        UPDATE games
        SET whiteUsername=?, blackUsername=?, gameName=?, game=?, gameOver=?
        WHERE gameID=?
        """;

        String gameJson = JsonUtils.toJson(game.game());

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {

            ps.setString(1, game.whiteUsername());
            ps.setString(2, game.blackUsername());
            ps.setString(3, game.gameName());
            ps.setString(4, gameJson);
            ps.setBoolean(5, game.gameOver());
            ps.setInt(6, game.gameID());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Unable to update game", e);
        }
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        var statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            ps.setString(1, auth.authToken());
            ps.setString(2, auth.username());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to create token", e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        var statement = "SELECT authToken, username FROM auth WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            ps.setString(1, authToken);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(rs.getString("authToken"), rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to authenticate", e);
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement("DELETE FROM auth WHERE authToken = ?")) {
                ps.setString(1, authToken);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear", e);
        }
    }
}