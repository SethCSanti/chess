package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    private ClearService clearService;

    @BeforeEach
    public void setUp() {
        clearService = new ClearService(new MemoryDataAccess());
    }

    @Test
    public void clearSuccess() throws DataAccessException {
        assertDoesNotThrow(() -> clearService.clear());
    }
}