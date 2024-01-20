import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

//Classes to Test
import at.fhtw.application.model.Card;


class CardTest {

    @Test
    void setName_DragonRightType() {
        //----- Preparation -----
        Card card = new Card();

        //----- Testing Area -----
        card.setName("Dragon");

        //----- Check results -----
        assertEquals("Fire", card.getElementType(), "Type should be 'Fire' not '" + card.getElementType() + "'!");
    }


    @Test
    void setName_KnightRightType() {
        //----- Preparation -----
        Card card = new Card();

        //----- Testing Area -----
        card.setName("Knight");

        //----- Check results -----
        assertEquals("Regular", card.getElementType(), "Type should be 'Regular' not '" + card.getElementType() + "'!");
    }

    @Test
    void getElementDamage_RightModifier_WaterVsFire() {
        //----- Preparation -----
        Card card1 = new Card("testID1", "FireGoblin", 40,  "Fire", "monster");
        Card card2 = new Card("testID2", "WaterSpell", 30,  "Water", "spell");

        //----- Testing Area -----
        double modifiedDamageFire = card1.getElementDamage(card2.getElementType());
        double modifiedDamageWater = card2.getElementDamage(card1.getElementType());

        //----- Check results -----
        assertEquals(card1.getDamage() * 0.5, modifiedDamageFire, "Fire is weak against Water so damage should be halved: " + card1.getDamage() + " -> " + card1.getDamage() * 0.5);
        assertEquals(card2.getDamage() * 2, modifiedDamageWater, "Water is strong against Fire so damage should be doubled: " + card2.getDamage() + " -> " + card2.getDamage() * 2);

    }

    @Test
    void getElementDamage_RightModifier_SameType() {
        //----- Preparation -----
        Card card1 = new Card("testID1", "WaterGoblin", 40,  "Water", "monster");
        Card card2 = new Card("testID2", "WaterSpell", 30,  "Water", "spell");

        //----- Testing Area -----
        double modifiedDamage1 = card1.getElementDamage(card2.getElementType());
        double modifiedDamage2 = card2.getElementDamage(card1.getElementType());

        //----- Check results -----
        assertEquals(card1.getDamage(), modifiedDamage1, "The Cards have the same type, damage should be unchanged: " + card1.getDamage());
        assertEquals(card2.getDamage(), modifiedDamage2, "The Cards have the same type, damage should be unchanged: " + card2.getDamage());

    }
}