package service;

import dataaccess.*;
import org.junit.jupiter.api.*;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserService(new MemoryDataAccess());
    }

    @Test
    public void registerSuccess() throws Exception {
        RegisterResult result = userService.registerUser(new RegisterRequest("seth", "pass123", "seth@email.com"));
        assertNotNull(result.getAuthToken());
        assertEquals("seth", result.getUsername());
    }

    @Test
    public void registerDuplicateUsername() throws Exception {
        userService.registerUser(new RegisterRequest("seth", "pass123", "seth@email.com"));
        assertThrows(AlreadyTakenException.class, () ->
                userService.registerUser(new RegisterRequest("seth", "pass456", "seth2@email.com")));
    }

    @Test
    public void registerMissingFields() {
        assertThrows(BadRequestException.class, () ->
                userService.registerUser(new RegisterRequest(null, "pass123", "seth@email.com")));
    }

    @Test
    public void loginSuccess() throws Exception {
        userService.registerUser(new RegisterRequest("seth", "pass123", "seth@email.com"));
        LoginResult result = userService.loginUser(new LoginRequest("seth", "pass123"));
        assertNotNull(result.getAuthToken());
        assertEquals("seth", result.getUsername());
    }

    @Test
    public void loginWrongPassword() throws Exception {
        userService.registerUser(new RegisterRequest("seth", "pass123", "seth@email.com"));
        assertThrows(UnauthorizedException.class, () ->
                userService.loginUser(new LoginRequest("seth", "wrongpass")));
    }

    @Test
    public void loginUserNotFound() {
        assertThrows(UnauthorizedException.class, () ->
                userService.loginUser(new LoginRequest("nobody", "pass123")));
    }

    @Test
    public void logoutSuccess() throws Exception {
        RegisterResult registered = userService.registerUser(new RegisterRequest("seth", "pass123", "seth@email.com"));
        assertDoesNotThrow(() ->
                userService.logoutUser(new LogoutRequest(registered.getAuthToken())));
    }

    @Test
    public void logoutInvalidToken() {
        assertThrows(UnauthorizedException.class, () ->
                userService.logoutUser(new LogoutRequest("invalidtoken")));
    }
}