import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

//imported
import at.fhtw.application.dal.repository.PackageRepositoryImpl;
import at.fhtw.application.dal.UnitOfWork;
import at.fhtw.application.model.Card;

class PackageRepositoryImplTest {

    @Test
    void testCheckAdminToken_PermissionDenied() throws SQLException {
        //----- Preparation -----
        UnitOfWork mockUnitOfWork = mock(UnitOfWork.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        //Behavior for mock objects
        when(mockUnitOfWork.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Simulate that admin token doesn't exist

        //PackageRepository with mock objects
        PackageRepositoryImpl packageRepository = new PackageRepositoryImpl(mockUnitOfWork);

        //Test data for admin token
        String adminToken = "bearer_hacker_token";

        //----- Testing Area -----
        boolean result = packageRepository.checkAdminToken(adminToken);

        //----- Check results -----
        //Expect false since permission is denied
        assertFalse(result);

        //Verify that the method made the expected calls
        verify(mockUnitOfWork).prepareStatement(anyString());
        verify(mockPreparedStatement).setString(anyInt(), eq(adminToken));
        verify(mockResultSet).next();
    }

    @Test
    void testCreateCard_CardAlreadyExists() throws SQLException {
        //----- Preparation -----
        UnitOfWork mockUnitOfWork = mock(UnitOfWork.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        //Behavior for mock objects
        when(mockUnitOfWork.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        //Simulate card already exists
        when(mockResultSet.next()).thenReturn(false);

        //PackageRepository with mock objects
        PackageRepositoryImpl packageRepository = new PackageRepositoryImpl(mockUnitOfWork);

        // Test data for creating a card
        String cardId = "1234";
        String name = "TestSpell";
        double damage = 10.0;

        // Call createCard method with mock objects
        String result = packageRepository.createCard(cardId, name, damage);

        // Verify expected result
        assertEquals("DOUBLE", result); // Expect "DOUBLE" since the card already exists

        // Verify that the method made the expected calls
        verify(mockUnitOfWork).prepareStatement(anyString());
        verify(mockPreparedStatement, times(3)).setString(anyInt(), anyString());
        verify(mockPreparedStatement).executeQuery();
        verify(mockResultSet).next();
    }
}