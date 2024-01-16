package at.fhtw.sampleapp.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//JUnit Classes
import static org.junit.jupiter.api.Assertions.*;
import org.testng.annotations.Test;

//Services to Test

@Getter
public class UnitTestClass {



    // Default constructor
    public UnitTestClass() {}

    @Test
    public void testBattle() {
        //----- Preparation -----
        //decks
        List<Card> testDeck1 = new ArrayList<>(List.of(
                new Card("testID1", "Dragon", 30,  "Fire", "monster"),
                new Card("testID2", "WaterSpell", 40,  "Regular", "spell"),
                new Card("testID3", "FireSpell", 50,  "Fire", "spell"),
                new Card("testID4", "WaterGoblin", 20,  "Water", "monster")
        ));

        List<Card> testDeck2 = new ArrayList<>(List.of(
                new Card("testID1", "RegularGoblin", 30,  "Regular", "monster"),
                new Card("testID2", "WaterSpell", 30,  "Regular", "spell"),
                new Card("testID3", "FireSpell", 30,  "Regular", "spell"),
                new Card("testID4", "WaterGoblin", 20,  "Water", "monster")
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


}
