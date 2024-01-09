package at.fhtw.sampleapp.dal.repository;

import at.fhtw.sampleapp.dal.DataAccessException;
import at.fhtw.sampleapp.dal.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class StatRepositoryImpl implements StatRepository{

    private final UnitOfWork unitOfWork;

    public StatRepositoryImpl(UnitOfWork unitOfWork)
    {
        this.unitOfWork = unitOfWork;
    }

    public String showStats(String username){

        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
            SELECT username, coins, eloscore FROM accounts
            WHERE username = ?
            """))
        {
            //Fill in the '?'
            preparedStatement.setString(1, username);


            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    do {
                        // Retrieve values from the current row
                        String cardId = resultSet.getString("username");
                        int coins = resultSet.getInt("coins");
                        int eloscore = resultSet.getInt("eloscore");

                        // Print or process the retrieved values
                        System.out.println("Stats from " + cardId + ": Coins: " + coins + " | Elo : " + eloscore);

                    } while (resultSet.next());

                    return "OK";
                } else {
                    System.out.println("An error occurred while searching for stats");
                    return "ERR";
                }
            } catch (SQLException e) {
                throw new DataAccessException("Query not successful", e);
            }



        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }


    public String showScoreboard(){

        //Is it important that the admin is invisible???
        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
            SELECT username, eloscore FROM accounts
            WHERE username != 'admin'
            order by eloscore
            desc
            """))
        {

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    do {
                        // Retrieve values from the current row
                        String cardId = resultSet.getString("username");
                        int eloscore = resultSet.getInt("eloscore");

                        // Print or process the retrieved values
                        System.out.println("Username: " + cardId + " | Elo : " + eloscore);

                    } while (resultSet.next());

                    return "OK";
                } else {
                    System.out.println("An error occurred while searching for stats");
                    return "ERR";
                }
            } catch (SQLException e) {
                throw new DataAccessException("Query not successful", e);
            }



        } catch (SQLException e) {
            throw new DataAccessException("Select not successful", e);
        }
    }


}
