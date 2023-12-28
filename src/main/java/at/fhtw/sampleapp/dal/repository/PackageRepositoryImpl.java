package at.fhtw.sampleapp.dal.repository;

import at.fhtw.sampleapp.dal.DataAccessException;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PackageRepositoryImpl implements PackageRepository{
    private final UnitOfWork unitOfWork;

    public PackageRepositoryImpl(UnitOfWork unitOfWork)
    {
        this.unitOfWork = unitOfWork;
    }

    //----------------------------- create Package -----------------------------

    //Check permission
    public boolean checkAdminToken(String loginToken) {
        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
                     SELECT * FROM accounts
                     WHERE username = 'admin' AND logintoken = ?
                     """)){
            //Fill the '?'
            preparedStatement.setString(1, loginToken);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Permission granted");
                    return  true;
                } else {
                    System.out.println("Permission denied");
                    return  false;
                }
            } catch (SQLException e) {
                throw new DataAccessException("Query not successful", e);
            }

        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }

    //create Cards based on the given data
    public String createCard(String id, String name, double damage) {
        //Insert card data and return primary key to return it
        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
                     INSERT INTO cards (ccid, name, damage)
                     VALUES (?, ?, ?)
                     ON CONFLICT (ccid) DO NOTHING
                     RETURNING ccid
                     """)){
            //Fill the '?'
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setDouble(3, damage);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String generatedKey = resultSet.getString("ccid");
                    System.out.println("Card was created successfully with ID: " + generatedKey);
                    return generatedKey;
                } else {
                    System.out.println("Error trying to create new Card");
                    //if an error occurred (There is already a card with the same ccid)
                    return "DOUBLE";
                }
            } catch (SQLException e) {
                throw new DataAccessException("Query not successful", e);
            }

        } catch (SQLException e) {
            throw new DataAccessException("Insert not successful", e);
        }
    }

    //Used to create a package made of 5 Cards and create cards in cards table
    public String fillPackage(String[] cardIDs){
        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
                     INSERT INTO packages (card1id, card2id, card3id, card4id, card5id)
                     VALUES (?, ?, ?, ?, ?)
                     """)){

            //index to fill prepared Statement
            int i = 1;

            //Fill in the '?'
            for (String cardID : cardIDs) {

                //insert cardIDs  in every card column
                preparedStatement.setString(i++, cardID);
            }

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Package was successfully created");
                return "OK";
            } else {
                System.out.println("Error trying to create a package");
                return "ERR";
            }

        } catch (SQLException e) {
            throw new DataAccessException("Insert not successful", e);
        }
    }

    //----------------------------- buy Package -----------------------------

    //checks if user is logged in and if the user has enough coins
    public boolean checkCredibility(String loginToken){
        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
                     SELECT * FROM accounts
                     WHERE loginToken = ? AND coins >= 5
                     """)){
            //Fill the '?'
            preparedStatement.setString(1, loginToken);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("User qualifies for buying a package");
                    return true;

                } else {
                    System.out.println("Error trying to buy package. Buy more Coins/ You are not logged in");
                    return false;
                }
            } catch (SQLException e) {
                throw new DataAccessException("Query not successful", e);
            }


        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }

    //get the first package
    public String[] chooseFirstPackage(){
        //create an cardArray
        String[] cardIDArray = new String[5];

        //LIMIT 1 uses the first element of the table for its output
        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
                     SELECT * FROM packages
                     LIMIT 1
                     """)){


            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("You got a package");

                    //fill Array with Card infos
                    for(int i = 0; i<5; ++i){
                        cardIDArray[i] = resultSet.getString(i + 2);
                    }


                } else {
                    System.out.println("There is no package Left to buy. Please try it later!");
                }
            } catch (SQLException e) {
                throw new DataAccessException("Query not successful", e);
            }

        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
        return cardIDArray;
    }


    //delete used up package
    public boolean deleteFirstPackage(){
        //LIMIT 1 uses the first element of the table for its output
        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
                     DELETE FROM packages
                     WHERE id = (SELECT id FROM packages ORDER BY id LIMIT 1)
                     """)) {

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("You deleted the opened package successfully");
                return true;
            } else {
                System.out.println("Error, trying to delete the package");
                return false;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Delete not successful", e);
        }
    }


    //If every requirement is met, subtract 5 coins
    public void finishTransaction(String loginToken, int costs){
        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
                     UPDATE accounts
                     SET coins = coins - ?
                     WHERE loginToken = ?
                     """)){
            //Fill the '?'
            preparedStatement.setInt(1, costs);
            preparedStatement.setString(2, loginToken);

            try {
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Payment was successful");
                } else {
                    System.out.println("An error occurred during payment");
                }
            } catch (SQLException e) {
                throw new DataAccessException("Query not successful", e);
            }

        } catch (SQLException e) {
            throw new DataAccessException("Update not successful", e);
        }
    }


    //User receives Cards
    public String receiveCards(String loginToken, String cardID){
        //split the token in two parts
        String[] parts = loginToken.split("-");

        //extract username out of token
        String username = parts[0];

        //SQL statement
        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
                     UPDATE cards
                     SET owner = ?
                     WHERE ccid = ?
                     """)){

            //Fill the '?'
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, cardID);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User received Card");
                return "OK";
            } else {
                System.out.println("An error while receiving the Card");
                return "ERR";
            }

        } catch (SQLException e) {
            throw new DataAccessException("Insert not successful", e);
        }
    }



}
