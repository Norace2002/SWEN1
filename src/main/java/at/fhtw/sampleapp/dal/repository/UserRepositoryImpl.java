package at.fhtw.sampleapp.dal.repository;

import at.fhtw.sampleapp.dal.DataAccessException;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.model.User;
import lombok.Getter;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class UserRepositoryImpl implements UserRepository{

    private final UnitOfWork unitOfWork;

    public UserRepositoryImpl(UnitOfWork unitOfWork)
    {
        this.unitOfWork = unitOfWork;
    }

    //Used to create a user or check if there is already an identical username
    public String createUser(String username, String password) {

        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
                    INSERT INTO accounts(username, password, coins, eloScore)
                    SELECT ?, ?, ? ,?
                    WHERE NOT EXISTS(
                        SELECT 1
                        FROM accounts
                        WHERE username = ?
                    );
                """))
        {
            //Fill the '?'
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setInt(3, 20);
            preparedStatement.setInt(4, 100);
            preparedStatement.setString(5, username);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                //For debugging and actual output
                System.out.println("User was successfully added");
                return "OK";
            } else {
                System.out.println("Username is already taken");
                return "ERR";
            }

        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }

    //Used for Login purposes
    public String compareUser(String username, String password) {
        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
            UPDATE accounts
            SET logintoken = ?
            WHERE username = ? AND password = ?
            """))
        {
            //Fill in the '?'
            preparedStatement.setString(1, username + "-mtcgToken");
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);


            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User was successfully logged in");
                return "OK";
            } else {
                System.out.println("Sorry, Username or Password are incorrect");
                return "ERR";
            }


        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }

    public String showCards(String loginToken) {
        if(loginToken.isEmpty()){
            System.out.println("Please log in first!");
            return "ERR";
        }
        //split the token in two parts
        String[] parts = loginToken.split("-");

        //extract username out of token
        String username = parts[0];

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


}
