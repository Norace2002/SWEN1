package at.fhtw.sampleapp.dal.repository;

import java.util.List;

public interface CardRepository {

    //Cards
    String showCards(String username);
    String showDeck(String username);
    String configureDeck(List<String> cardIds, String username);


}
