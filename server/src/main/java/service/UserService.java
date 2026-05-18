package service;

import dataaccess.DataAccess;
import model.*;
import dataaccess.*;
import model.UserData;

import java.util.Collection;

public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }
}
