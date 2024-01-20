import at.fhtw.application.model.BattleLogic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

//Classes to Test
import at.fhtw.application.model.Card;
import at.fhtw.application.model.User;
import at.fhtw.application.model.BattleLogic;

import java.util.ArrayList;
import java.util.List;

class BattleLogicTest {

    @Mock
    private User user1;

    @Mock
    private User user2;

    @InjectMocks
    private BattleLogic battleLogic;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testBattle_WinnerPlayer1() {
        //----- Preparation -----
        //decks
        List<Card> testDeck1 = new ArrayList<>(List.of(
                new Card("testID1", "Dragon", 20,  "Fire", "monster"),
                new Card("testID2", "FireSpell", 20,  "Fire", "spell"),
                new Card("testID3", "FireSpell", 20,  "Fire", "spell"),
                new Card("testID4", "FireGoblin", 20,  "Fire", "monster")
        ));

        List<Card> testDeck2 = new ArrayList<>(List.of(
                new Card("testID5", "RegularGoblin", 20,  "Regular", "monster"),
                new Card("testID6", "RegularSpell", 20,  "Regular", "spell"),
                new Card("testID7", "RegularSpell", 20,  "Regular", "spell"),
                new Card("testID8", "RegularGoblin", 20,  "Regular", "monster")
        ));

        //users
        User testUser1 = new User("testUser1", testDeck1);
        User testUser2 = new User("testUser2", testDeck2);

        //battleinfo
        List<String> battleInfo = new ArrayList<>();

        //battleLogic
        BattleLogic battleLogic = new BattleLogic(testUser1, testUser2);

        //----- Testing Area -----
        battleInfo = battleLogic.start();

        //----- Check results -----
        assertEquals(testUser1.getUsername(), battleInfo.get(0), "The winner should be " + battleInfo.get(0));
    }

    @Test
    void setUser_Test() {
        //----- Preparation -----
        BattleLogic battleLogic = new BattleLogic(user1, user2);

        List<Card> testDeck = new ArrayList<>(List.of(
                new Card("testID1", "Dragon", 20,  "Fire", "monster"),
                new Card("testID2", "FireSpell", 20,  "Fire", "spell"),
                new Card("testID3", "FireSpell", 20,  "Fire", "spell"),
                new Card("testID4", "FireGoblin", 20,  "Fire", "monster")
        ));

        User testUser = new User("testuser", testDeck);

        //----- Testing Area -----
        battleLogic.setUser1(testUser);

        //----- Check results -----
        assertEquals("testuser", battleLogic.getUser1().getUsername(), "Username should be testUser");
    }

    @Test
    void getRewardUser1() {
        //----- Preparation -----
        //decks
        List<Card> testDeck1 = new ArrayList<>(List.of(
                new Card("testID1", "Dragon", 20,  "Fire", "monster"),
                new Card("testID2", "FireSpell", 20,  "Fire", "spell"),
                new Card("testID3", "FireSpell", 20,  "Fire", "spell"),
                new Card("testID4", "FireGoblin", 20,  "Fire", "monster")
        ));

        List<Card> testDeck2 = new ArrayList<>(List.of(
                new Card("testID5", "RegularGoblin", 20,  "Regular", "monster"),
                new Card("testID6", "RegularSpell", 20,  "Regular", "spell"),
                new Card("testID7", "RegularSpell", 20,  "Regular", "spell"),
                new Card("testID8", "RegularGoblin", 20,  "Regular", "monster")
        ));

        //users
        User testUser1 = new User("testUser1", testDeck1);
        User testUser2 = new User("testUser2", testDeck2);

        //reward Card
        Card rewardCard = new Card("testID5", "RegularGoblin", 20,  "Regular", "monster");

        BattleLogic battleLogic = new BattleLogic(testUser1, testUser2);

        //----- Testing Area -----
        battleLogic.getRewardUser1(rewardCard);

        //----- Check results -----
        assertEquals(5, testDeck1.size(), "Due to Round 1 victory, deck.size() from testUser1 should be 5");
        assertEquals(3, testDeck2.size(), "Due to Round 1 loss, deck.size() from testUser2 should be 3");
    }

    @Test
    void testExecuteRound_DragonVsGoblin() {
        //----- Preparation -----
        Card user1card = new Card("testID1", "Dragon", 20,  "Fire", "monster");
        Card user2card = new Card("testID2", "RegularGoblin", 30,  "Regular", "monster");

        //Create a spy for the BattleLogic because getRewardUser1 is called on the real Object not the mocked one
        BattleLogic battleLogicSpy = spy(new BattleLogic(user1, user2));

        //----- Testing Area -----
        //to call the real methods without triggering them
        doNothing().when(battleLogicSpy).getRewardUser1(user2card);

        battleLogicSpy.executeRound(user1card, user2card);

        //----- Check results -----
        //Verify that the correct method was called
        verify(battleLogicSpy).getRewardUser1(user2card);
    }

    @Test
    void testExecuteRound_DragonVsFireElf() {
        //----- Preparation -----
        Card user1card = new Card("testID1", "Dragon", 50,  "Fire", "monster");
        Card user2card = new Card("testID2", "FireElf", 30,  "Fire", "monster");

        //Create a spy for the BattleLogic because getRewardUser1 is called on the real Object not the mocked one
        BattleLogic battleLogicSpy = spy(new BattleLogic(user1, user2));

        // ----- Testing Area -----
        // to call the real methods without triggering them
        doNothing().when(battleLogicSpy).getRewardUser2(user1card);

        battleLogicSpy.executeRound(user1card, user2card);

        //----- Check results -----
        //Verify that the correct method was called
        verify(battleLogicSpy).getRewardUser2(user1card);
    }

    // Add more tests for other scenarios...

    // Example:
    @Test
    void testExecuteRound_KnightVsWater() {
        //----- Preparation -----
        Card user1card = new Card("testID1", "Knight", 50,  "Fire", "monster");
        Card user2card = new Card("testID2", "WaterSpell", 10,  "Water", "spell");

        // Create a spy for the BattleLogic because getRewardUser2 is called on the real Object not the mocked one
        BattleLogic battleLogicSpy = spy(new BattleLogic(user1, user2));

        //----- Testing Area -----
        //to call the real methods without triggering them
        doNothing().when(battleLogicSpy).getRewardUser2(user1card);

        battleLogicSpy.executeRound(user1card, user2card);

        //----- Check results -----
        //Verify that the correct method was called
        verify(battleLogicSpy).getRewardUser2(user1card);
    }

    @Test
    void testExecuteRound_HarlequinEvenDeck() {
        //----- Preparation -----
        List<Card> testDeck1 = new ArrayList<>(List.of(
                new Card("testID1", "Harlequin", 50,  "Regular", "monster"),
                new Card("testID2", "FireSpell", 20,  "Fire", "spell")
        ));

        Card user1card = new Card("testID1", "Harlequin", 10,  "Regular", "monster");
        Card user2card = new Card("testID3", "WaterGoblin", 50,  "Water", "monster");

        User testUser1 = new User("testUser1", testDeck1);

        // Create a spy for the BattleLogic because getRewardUser1 is called on the real Object not the mocked one
        BattleLogic battleLogicSpy = spy(new BattleLogic(testUser1, user2));

        //----- Testing Area -----
        // to call the real methods without triggering them
        doNothing().when(battleLogicSpy).getRewardUser1(user2card);

        battleLogicSpy.executeRound(user1card, user2card);

        //----- Check results -----
        //Verify that the correct method was called
        verify(battleLogicSpy).getRewardUser1(user2card);
    }

    @Test
    void testExecuteRound_HarlequinOddDeck() {
        //----- Preparation -----
        List<Card> testDeck2 = new ArrayList<>(List.of(
                new Card("testID2", "Harlequin", 50,  "Regular", "monster")
        ));

        Card user1card = new Card("testID1", "Knight", 10,  "Regular", "monster");
        Card user2card = new Card("testID2", "Harlequin", 50,  "Regular", "monster");

        User testUser2 = new User("testUser2", testDeck2);


        // Create a spy for the BattleLogic because getRewardUser1 is called on the real Object not the mocked one
        BattleLogic battleLogicSpy = spy(new BattleLogic(user1, testUser2));

        //----- Testing Area -----
        //to call the real methods without triggering them
        doNothing().when(battleLogicSpy).getRewardUser2(user2card);

        battleLogicSpy.executeRound(user1card, user2card);

        //----- Check results -----
        //Verify that the correct method was called
        verify(battleLogicSpy).getRewardUser1(user2card);
    }

    @Test
    void testExecuteRound_BothHarlequin() {
        //----- Preparation -----
        Card user1card = new Card("testID1", "Harlequin", 10,  "Regular", "monster");
        Card user2card = new Card("testID2", "Harlequin", 15,  "Regular", "monster");


        // Create a spy for the BattleLogic because getRewardUser1 is called on the real Object not the mocked one
        BattleLogic battleLogicSpy = spy(new BattleLogic(user1, user2));

        //----- Testing Area -----
        battleLogicSpy.executeRound(user1card, user2card);

        //----- Check results -----
        //Verify that the correct method was called
        verify(battleLogicSpy.getUser1()).loseCard(user1card);
        verify(battleLogicSpy.getUser2()).loseCard(user2card);
    }


}