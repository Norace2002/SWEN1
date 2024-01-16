package at.fhtw.sampleapp.dal.repository;

import at.fhtw.sampleapp.dal.DataAccessException;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.model.Card;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
                    System.out.println("***************************************************************");
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
                    System.out.println("***************************************************************");
                    return "EMPTY";
                }
            } catch (SQLException e) {
                throw new DataAccessException("Query not successful", e);
            }



        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }


    public void resetDeck(String username){
        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
            UPDATE cards
            SET indeck = false
            WHERE owner = ?
            """))
        {
            //Fill in the '?'
            preparedStatement.setString(1, username);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Deck has been reset");
            }
            else {
                System.out.println("Error, trying to reset teh deck");
            }


        } catch (SQLException e) {
            throw new DataAccessException("Update not successful", e);
        }
    }


    public String configureDeck(List<String> cardIds, String username){
        //To track how many cards are in the deck
        int cardCount = 0;

        //check if there are duplicates the given set of 4 cards
        for (int i = 0; i < cardIds.size(); ++i){
            for(int j = 0; j< cardIds.size(); ++j){
                if(i != j && Objects.equals(cardIds.get(i), cardIds.get(j))){
                    return "wrongAmount";
                }
            }
        }

        //looping through chosen set of 4 cards and put them in the deck
        for (String cardId : cardIds) {
            try (PreparedStatement preparedStatement =
                         this.unitOfWork.prepareStatement("""
            UPDATE cards
            SET indeck = true
            WHERE ccid = ? AND owner = ?
                AND NOT EXISTS (
                    SELECT 1
                    FROM tradings
                    WHERE cardtotrade = ?
                );
            """))
            {
                //Fill in the '?'
                preparedStatement.setString(1, cardId);
                preparedStatement.setString(2, username);
                preparedStatement.setString(3, cardId);


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

        return "wrongAmount";
    }


    public String showTrades(String username) {

        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
            SELECT * FROM tradings
            WHERE username = ?
            """))
        {
            //Fill in the '?'
            preparedStatement.setString(1, username);


            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                System.out.println("*********************** Trades offered from " + username + " **************************");

                if (resultSet.next()) {
                    do {
                        // Retrieve values from the current row
                        String cardToTradeID = resultSet.getString("cardtotrade");
                        String type = resultSet.getString("type");
                        double minDamage = resultSet.getDouble("mindamage");

                        // Print or process the retrieved values
                        System.out.println("----------------------");
                        System.out.println("Card to Trade ID: " + cardToTradeID);
                        System.out.println("Wanted Type: " + type);
                        System.out.println("Minimal damage: " + minDamage);
                    } while (resultSet.next());

                    System.out.println("----------------------");
                    System.out.println("***************************************************************");
                    return "OK";
                } else {
                    System.out.println("No trades found for " + username);
                    System.out.println("***************************************************************");
                    return "EMPTY";
                }
            } catch (SQLException e) {
                throw new DataAccessException("Query not successful", e);
            }



        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }

    public String checkDoubleTrades(String ttid) {

        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
            SELECT * FROM tradings
            WHERE ttid = ?
            """))
        {
            //Fill in the '?'
            preparedStatement.setString(1, ttid);


            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Deal ID exists already");
                    return "DOUBLE";
                } else {
                    System.out.println("Deal ID not existing yet");
                    return "NOTDOUBLE";
                }
            } catch (SQLException e) {
                throw new DataAccessException("Query not successful", e);
            }



        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }


    public String createTrade(String username, String ttid, String cardToTradeID, String cardType, String minDamage) {

        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
                    INSERT INTO tradings(ttid, username, cardtotrade, type, mindamage)
                    SELECT ?, ?, ? ,?, ?
                    WHERE EXISTS(
                        SELECT *
                        FROM cards
                        WHERE owner = ? AND indeck = false AND ccid = ?
                    );
                """))
        {
            //Fill the '?'
            preparedStatement.setString(1, ttid);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, cardToTradeID);
            preparedStatement.setString(4, cardType);
            preparedStatement.setDouble(5, Double.parseDouble(minDamage));
            preparedStatement.setString(6, username);
            preparedStatement.setString(7, cardToTradeID);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                //For debugging and actual output
                System.out.println("Trade was successfully added");
                return "OK";
            } else {
                System.out.println("The deal contains a card that is not owned by the user or locked in the deck");
                return "FORBIDDEN";
            }

        } catch (SQLException e) {
            throw new DataAccessException("Insert not successful", e);
        }
    }


    public String deleteTrade(String username, String ttid){
        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
                     DELETE FROM tradings
                     WHERE username = ? AND ttid = ?
                     """)) {

            //Fill the '?'
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, ttid);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("You deleted the trade successfully");
                return "OK";
            } else {
                System.out.println("The deal contains a card that is not owned by the user.");
                return "FORBIDDEN";
            }

        } catch (SQLException e) {
            throw new DataAccessException("Delete not successful", e);
        }
    }


    public List<String> acceptTrade(String username) {
        //infos like username and id from cardtotrade
        List<String> tradeInfos = new ArrayList<>();
        //joins to tables to compare type and damage quickly
        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
                    SELECT t.username, t.cardtotrade, t.minDamage, t.type as tradetype, c.damage, c.type as cardtype
                    FROM cards c
                    JOIN tradings t ON c.ccid = t.cardtotrade
                    WHERE EXISTS(
                        SELECT *
                        FROM cards
                        WHERE owner = 'altenhof' AND ccid = '951e886a-0fbf-425d-8df5-af2ee4830d85' AND indeck = false
                    );
                """))
        {

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt("mindamage") <= resultSet.getInt("damage")
                        && Objects.equals(resultSet.getString("tradetype"), resultSet.getString("cardtype"))) {
                    System.out.println(username + " has the the specified card");
                    tradeInfos.add(resultSet.getString("username"));
                    tradeInfos.add(resultSet.getString("cardtotrade"));
                } else {
                    System.out.println(username + " does not have the specified card");
                    tradeInfos.add("FORBIDDEN");
                }
            } catch (SQLException e) {
                throw new DataAccessException("Query not successful", e);
            }

        } catch (SQLException e) {
            throw new DataAccessException("Select/Join not successful", e);
        }
        return tradeInfos;
    }

    public void changeOwner(String newOwner, String ccid, String oldOwner){
        System.out.println("newOwner: " + newOwner);
        System.out.println("ccid: " + ccid);
        System.out.println("oldOwner: " + oldOwner);

        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
        UPDATE cards
        SET owner = ?
        WHERE ccid = ? AND owner = ?
        """))
        {
            //Fill in the '?'
            preparedStatement.setString(1, newOwner);
            preparedStatement.setString(2, ccid);
            preparedStatement.setString(3, oldOwner);


            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Ownership change complete");
            }
            else {
                System.out.println("An error occurred during changing ownership");
            }


        } catch (SQLException e) {
            throw new DataAccessException("Update not successful", e);
        }
    }



}
