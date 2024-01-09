package at.fhtw.sampleapp.dal.repository;

import at.fhtw.sampleapp.dal.DataAccessException;
import at.fhtw.sampleapp.dal.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CardRepositoryImpl implements CardRepository{

    private final UnitOfWork unitOfWork;

    public CardRepositoryImpl(UnitOfWork unitOfWork)
    {
        this.unitOfWork = unitOfWork;
    }

    public String showCards(String username) {

        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
            SELECT * FROM cards
            WHERE owner = ?
            """))
        {
            //Fill in the '?'
            preparedStatement.setString(1, username);


            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                System.out.println("*********************** Cards from " + username + " ***************************");

                if (resultSet.next()) {
                    do {
                        // Retrieve values from the current row
                        String cardId = resultSet.getString("ccid");
                        String name = resultSet.getString("name");
                        double damage = resultSet.getDouble("damage");

                        // Print or process the retrieved values
                        System.out.println("----------------------");
                        System.out.println("Card ID: " + cardId);
                        System.out.println("Name: " + name);
                        System.out.println("Damage: " + damage);
                    } while (resultSet.next());

                    System.out.println("----------------------");
                    System.out.println("***************************************************************");
                    return "OK";
                } else {
                    System.out.println("No cards found for " + username);
                    return "EMPTY";
                }
            } catch (SQLException e) {
                throw new DataAccessException("Query not successful", e);
            }



        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }

    public String showDeck(String username){

        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
            SELECT * FROM cards
            WHERE owner = ? AND indeck = true
            """))
        {
            //Fill in the '?'
            preparedStatement.setString(1, username);


            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                System.out.println("*********************** " + username + "'s battledeck***************************");

                if (resultSet.next()) {
                    do {
                        // Retrieve values from the current row
                        String cardId = resultSet.getString("ccid");
                        String name = resultSet.getString("name");
                        double damage = resultSet.getDouble("damage");

                        // Print or process the retrieved values
                        System.out.println("----------------------");
                        System.out.println("Card ID: " + cardId);
                        System.out.println("Name: " + name);
                        System.out.println("Damage: " + damage);
                    } while (resultSet.next());

                    System.out.println("----------------------");
                    System.out.println("***************************************************************");
                    return "OK";
                } else {
                    System.out.println(username + "'s battledeck is empty.");
                    return "EMPTY";
                }
            } catch (SQLException e) {
                throw new DataAccessException("Query not successful", e);
            }



        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }


    public String configureDeck(List<String> cardIds, String username){
        //To track how many cards are in the deck
        int cardCount = 0;

        for (String cardId : cardIds) {
            try (PreparedStatement preparedStatement =
                         this.unitOfWork.prepareStatement("""
            UPDATE cards
            SET indeck = true
            WHERE ccid = ? AND owner = ?
            """))
            {
                //Fill in the '?'
                preparedStatement.setString(1, cardId);
                preparedStatement.setString(2, username);


                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    cardCount++;
                    System.out.println(cardCount+ " card was/were successfully selected");
                }
                else {
                    System.out.println("You do not have a specific card");
                    return "cardFailure";
                }


            } catch (SQLException e) {
                throw new DataAccessException("Update not successful", e);
            }
        }

        if(cardCount == 4){
            return "OK";
        }
        System.out.print("Test 3");
        return "wrongAmount";
    }


}
