package service;

import dataaccess.DataAccess;
import model.*;
import dataaccess.*;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import java.util.UUID;

public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    /** Registers a new user, creates an auth token, and returns the result. */
    public RegisterResult registerUser(RegisterRequest request) throws BadRequestException, AlreadyTakenException, DataAccessException {
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new BadRequestException("Make sure to input a username, password, and email");
        } else if (dataAccess.getUser(request.username()) != null) {
            throw new AlreadyTakenException("Username is already taken");
        } else {
            String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());
            dataAccess.createUser(new UserData(request.username(), hashedPassword, request.email()));
            String authToken = UUID.randomUUID().toString();
            dataAccess.createAuth(new AuthData(authToken, request.username()));
            return new RegisterResult(request.username(), authToken);
        }
    }

    /** Logs in an existing user, creates a new auth token, and returns the result. */
    public LoginResult loginUser(LoginRequest request) throws DataAccessException, BadRequestException, UnauthorizedException {
        if (request.username() == null || request.password() == null) {
            throw new BadRequestException("Make sure to input the right username and password");
        }
        UserData storedUser = dataAccess.getUser(request.username());
        if (storedUser == null) {
            throw new UnauthorizedException("Unauthorized");
        }
        try {
            if (!BCrypt.checkpw(request.password(), storedUser.password())) {
                throw new UnauthorizedException("Your password is incorrect");
            }
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Your password is incorrect");
        }
        String authToken = UUID.randomUUID().toString();
        dataAccess.createAuth(new AuthData(authToken, request.username()));
        return new LoginResult(request.username(), authToken);
    }

    /** Logs out a user by deleting their auth token. */
    public void logoutUser(LogoutRequest request) throws DataAccessException, UnauthorizedException {
        String authToken = request.authToken();
        if (dataAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException("User does not exist");
        } else {
            dataAccess.deleteAuth(authToken);
        }
    }
}
