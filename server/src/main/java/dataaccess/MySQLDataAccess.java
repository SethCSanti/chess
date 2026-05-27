package dataaccess;

import model.*;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.*;

public class MySQLDataAccess implements DataAccess {

    public MySQLDataAccess() throws DataAccessException {
        initializeDatabase();
    }

    private void initializeDatabase() throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException { }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        var statement = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(statement)) {
            ps.setString(1, user.username());
            ps.setString(2, hashedPassword);
            ps.setString(3, user.email());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new  DataAccessException("Unable to create user", e);
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
    public void createAuth(AuthData auth) throws DataAccessException { }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException { return null; }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException { }
}