package at.fhtw.application.dal.repository;

import at.fhtw.application.model.Card;

import java.util.List;

public interface BattleRepository {
    List<Card> getDeck(String username);

}
