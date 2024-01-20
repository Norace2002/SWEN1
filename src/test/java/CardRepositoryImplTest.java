import at.fhtw.application.dal.UnitOfWork;
import at.fhtw.application.dal.repository.CardRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

class CardRepositoryImplTest {

    @Test
    void testConfigureDeck_UserHasCards() throws SQLException {
        //----- Preparation -----
        UnitOfWork mockUnitOfWork = mock(UnitOfWork.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        //Behavior for mock objects
        when(mockUnitOfWork.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Successful update call

        //CardRepository with mock objects
        CardRepositoryImpl cardRepository = new CardRepositoryImpl(mockUnitOfWork);

        //Test data for card IDs and username
        List<String> cardIds = Arrays.asList("1234", "5678", "9012", "3456");
        String username = "testUser";

        //----- Testing Area -----
        String result = cardRepository.configureDeck(cardIds, username);

        //----- Check results -----
        assertEquals("OK", result);

        //Verify that the method made the expected calls
        verify(mockUnitOfWork, times(4)).prepareStatement(anyString());
        verify(mockPreparedStatement, times(12)).setString(anyInt(), anyString());
        verify(mockPreparedStatement, times(4)).executeUpdate();
    }

    @Test
    void testConfigureDeck_UserMissing1Card() throws SQLException {
        //----- Preparation -----
        UnitOfWork mockUnitOfWork = mock(UnitOfWork.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        //Behavior for mock objects
        when(mockUnitOfWork.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Successful update call

        when(mockPreparedStatement.executeUpdate())
                .thenReturn(1) //Successful update call
                .thenReturn(1)
                .thenReturn(1)
                .thenReturn(0); //Simulates a case where the user doesn't own one card

        //CardRepository with mock objects
        CardRepositoryImpl cardRepository = new CardRepositoryImpl(mockUnitOfWork);

        //Test data for card IDs and username
        List<String> cardIds = Arrays.asList("1234", "5678", "9012", "3456");
        String username = "testUser";

        //----- Testing Area -----
        String result = cardRepository.configureDeck(cardIds, username);

        //----- Check results -----
        assertEquals("cardFailure", result); // Expect "cardFailure" due to the user not owning one of the cards

        // Verify that the method made the expected calls
        verify(mockUnitOfWork, times(4)).prepareStatement(anyString());
        verify(mockPreparedStatement, times(12)).setString(anyInt(), anyString());
        verify(mockPreparedStatement, times(4)).executeUpdate();
    }

    @Test
    void testConfigureDeck_DuplicateCard() throws SQLException {
        //----- Preparation -----
        UnitOfWork mockUnitOfWork = mock(UnitOfWork.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        //Behavior for mock objects
        when(mockUnitOfWork.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Successful update call

        //Successful update calls
        when(mockPreparedStatement.executeUpdate())
                .thenReturn(1)
                .thenReturn(1)
                .thenReturn(1)
                .thenReturn(1);

        //CardRepository with mock objects
        CardRepositoryImpl cardRepository = new CardRepositoryImpl(mockUnitOfWork);

        //Test data for card IDs and username with a duplicate card '5678'
        List<String> cardIds = Arrays.asList("1234", "5678", "9012", "5678"); // Duplicate card ID
        String username = "testUser";

        //----- Testing Area -----
        String result = cardRepository.configureDeck(cardIds, username);

        //----- Check results -----
        //Expect "wrongAmount" due to the duplicate card
        assertEquals("wrongAmount", result);

        //Verify that the method were not called
        verify(mockUnitOfWork, times(0)).prepareStatement(anyString());
        verify(mockPreparedStatement, times(0)).setString(anyInt(), anyString()); // Increased to 16 due to the duplicate card
        verify(mockPreparedStatement, times(0)).executeUpdate();
    }

    @Test
    void testShowTrades() throws SQLException {
        //----- Preparation -----
        UnitOfWork mockUnitOfWork = mock(UnitOfWork.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        //Behavior for mock objects
        when(mockUnitOfWork.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        //Simulate one trade in the result set
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("cardtotrade")).thenReturn("1234");
        when(mockResultSet.getString("type")).thenReturn("spell");
        when(mockResultSet.getDouble("mindamage")).thenReturn(10.0);

        //CardRepository with mock objects
        CardRepositoryImpl cardRepository = new CardRepositoryImpl(mockUnitOfWork);

        //Test data for username
        String username = "testUser";

        //----- Testing Area -----
        String result = cardRepository.showTrades(username);

        //----- Check results -----
        assertEquals("OK", result);

        //Verify that the method made the expected calls
        verify(mockUnitOfWork).prepareStatement("""
            SELECT * FROM tradings
            WHERE username = ?
            """);
        verify(mockPreparedStatement).setString(1, username);
        verify(mockPreparedStatement).executeQuery();

        verify(mockResultSet).getString("cardtotrade");
        verify(mockResultSet).getString("type");
        verify(mockResultSet).getDouble("mindamage");
    }

    @Test
    void testCreateTrade_CardInDeck() throws SQLException {
        //----- Preparation -----
        UnitOfWork mockUnitOfWork = mock(UnitOfWork.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        //Behavior for mock objects
        when(mockUnitOfWork.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(0); // No rows affected, indicating the card is in the deck

        //CardRepository with mock objects
        CardRepositoryImpl cardRepository = new CardRepositoryImpl(mockUnitOfWork);

        //Test data
        String username = "testUser";
        String ttid = "123";
        String cardToTradeID = "456";
        String cardType = "Type";
        String minDamage = "5.0";

        //----- Testing Area -----
        String result = cardRepository.createTrade(username, ttid, cardToTradeID, cardType, minDamage);

        //----- Check results -----
        assertEquals("FORBIDDEN", result);

        //Verify that the method made the expected calls
        verify(mockUnitOfWork).prepareStatement(anyString());
        verify(mockPreparedStatement, times(6)).setString(anyInt(), anyString());
        verify(mockPreparedStatement).executeUpdate();
    }
}