package at.fhtw.sampleapp.dal.repository;

import at.fhtw.sampleapp.model.Card;

import java.sql.ResultSet;
import java.util.List;

public interface BattleRepository {
    List<Card> getDeck(String username);

}
