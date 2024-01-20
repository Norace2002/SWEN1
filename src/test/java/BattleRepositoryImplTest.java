import static org.junit.jupiter.api.Assertions.assertEquals;
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
import at.fhtw.application.dal.repository.BattleRepositoryImpl;
import at.fhtw.application.dal.UnitOfWork;
import at.fhtw.application.model.Card;

@ExtendWith(MockitoExtension.class)
class BattleRepositoryImplTest {

    @Mock
    private UnitOfWork unitOfWork;

    //to inject the mocks into BattleRepositoryImpl instance
    @InjectMocks
    private BattleRepositoryImpl battleRepository;

    @Test
    void testAdjustELO() throws SQLException {
        //----- Preparation -----
        String username = "testUser";
        int value = 10;
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        when(unitOfWork.prepareStatement(any())).thenReturn(preparedStatementMock);
        //Simulate one row affected
        when(preparedStatementMock.executeUpdate()).thenReturn(1);

        //----- Testing Area -----
        String result = battleRepository.adjustELO(username, value);

        //----- Check results -----
        assertEquals("OK", result);

        //To verify that the methods were called with the expected arguments
        verify(unitOfWork).prepareStatement("""
            UPDATE accounts
            SET eloscore = eloscore + ?
            WHERE username = ?
            """);
        verify(preparedStatementMock).setInt(1, value);
        verify(preparedStatementMock).setString(2, username);
    }
}
