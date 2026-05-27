package service;

import dataaccess.DataAccess;
import model.*;
import dataaccess.*;

import java.sql.SQLException;

public class ClearService {

    private final DataAccess dataAccess;

    public ClearService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear() throws DataAccessException, SQLException {
            dataAccess.clear();
    }
}
