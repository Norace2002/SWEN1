package at.fhtw.sampleapp.dal.repository;

import java.util.List;

public interface CardRepository {

    //Cards
    String showCards(String username);
    String showDeck(String username);
    void resetDeck(String username);
    String configureDeck(List<String> cardIds, String username);

    //Trading
    String showTrades(String username);
    String checkDoubleTrades(String ttid);
    String createTrade(String username, String ttid, String cardToTradeID, String cardType, String minDamage);
    String deleteTrade(String username, String ttid);
    List<String> acceptTrade(String username);
    void changeOwner(String newOwner, String ccid, String oldOwner);


}
