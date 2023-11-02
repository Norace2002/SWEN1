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

    public void createUser(String username, String password) {
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
            //Fill the '?' // Später username und Passwort mit mitgegeben Werten abspeichern
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setInt(3, 20);
            preparedStatement.setInt(4, 100);
            preparedStatement.setString(5, username);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User was successfully added");
            } else {
                System.out.println("Username is already taken");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }
}
