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
import java.util.List;
import java.util.Map;

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


    public String showUserdata(String username){
        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
            SELECT username, name, bio, image FROM accounts
            WHERE username = ?
            """))
        {
            //Fill in the '?'
            preparedStatement.setString(1, username);


            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    do {
                        // Retrieve values from the current row
                        String name = resultSet.getString("name");
                        String bio = resultSet.getString("bio");
                        String image = resultSet.getString("image");

                        // Print or process the retrieved values
                        System.out.println("Profil from " + username + ": Name: " + name + " | Bio : " + bio + " | Image : " + image);

                    } while (resultSet.next());

                    return "OK";
                } else {
                    System.out.println("User not found");
                    return "NotFound";
                }
            } catch (SQLException e) {
                throw new DataAccessException("Query not successful", e);
            }



        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }


    public String fillUserdata(String username, String name, String bio, String image){
        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
            UPDATE accounts
            SET name = ?,
                bio = ?,
                image = ?
            WHERE username = ?
            """))
        {
            //Fill in the '?'
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, bio);
            preparedStatement.setString(3, image);
            preparedStatement.setString(4, username);


            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User data was successfully changed");
                return "OK";
            }
            else {
                System.out.println("User not found");
                return "NotFound";
            }

        } catch (SQLException e) {
            throw new DataAccessException("Update not successful", e);
        }

    }

}
