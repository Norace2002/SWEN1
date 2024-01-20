package at.fhtw.application.dal.repository;

import at.fhtw.application.dal.DataAccessException;
import at.fhtw.application.dal.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import at.fhtw.application.model.Card;
import java.util.ArrayList;
import java.util.List;

public class BattleRepositoryImpl implements BattleRepository{

    private final UnitOfWork unitOfWork;

    public BattleRepositoryImpl(UnitOfWork unitOfWork)
    {
        this.unitOfWork = unitOfWork;
    }
    List<Card> deck = new ArrayList<>();

    public List<Card> getDeck(String username){
        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
            SELECT * FROM cards
            WHERE owner = ? AND indeck = true
            """))
        {
            //Fill in the '?'
            preparedStatement.setString(1, username);


            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    // Extract data from query into List<Cards>
                    Card card = new Card();
                    card.setId(resultSet.getString("ccid"));
                    card.setName(resultSet.getString("name"));
                    card.setCardType(resultSet.getString("type"));
                    card.setDamage(resultSet.getDouble("damage"));

                    //Add the card object to deck
                    deck.add(card);
                }
            } catch (SQLException e) {
                throw new DataAccessException("Query not successful", e);
            }



        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
        return deck;
    }


    public String adjustELO(String username, int value){
        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
            UPDATE accounts
            SET eloscore = eloscore + ?
            WHERE username = ?
            """))
        {
            //Fill in the '?'
            preparedStatement.setInt(1, value);
            preparedStatement.setString(2, username);


            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Eloscore from " + username + " was changed correctly");
                return "OK";
            }
            else {
                System.out.println("Error, trying to change eloscore from " + username);
                return "ERR";
            }



        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }

}
