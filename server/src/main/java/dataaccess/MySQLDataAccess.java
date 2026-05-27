package dataaccess;

import model.*;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.*;

public class MySQLDataAccess implements DataAccess {

    public MySQLDataAccess() throws DataAccessException {
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
            game          TEXT         NOT NULL
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
        try (var conn = DatabaseManager.getConnection();) {
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
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            ps.setString(1, user.username());
            ps.setString(2, hashedPassword);
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
    public int createGame(GameData game) throws DataAccessException { return 0; }

    @Override
    public GameData getGame(int gameID) throws DataAccessException { return null; }

    @Override
    public List<GameData> listGames() throws DataAccessException { return null; }

    @Override
    public void updateGame(GameData game) throws DataAccessException { }

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
            throw new DataAccessException("Unable to get user", e);
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();) {
            try (var ps = conn.prepareStatement("DELETE FROM auth WHERE authToken = ?")) {
                ps.setString(1, authToken);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Unable to clear", e);
        }
    }
}