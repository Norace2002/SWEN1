package at.fhtw.application.service;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Response;
import at.fhtw.application.dal.UnitOfWork;
import at.fhtw.application.dal.repository.StatRepositoryImpl;

import java.util.*;

//Test import


public class StatService extends AbstractService {

    public StatService() {}

    public Response showStatsPerRepository(String username) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String serverResponse = "Failure";

        //check if user is logged in
        if(username.isEmpty()){
            System.out.println("Please log in first!");
        }

        try (unitOfWork){
            //create Repository and shows the user's stats
            serverResponse = new StatRepositoryImpl(unitOfWork).showStats(username);

            //commit changed db entry
            unitOfWork.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();

            throw new RuntimeException(e);
        }

        //Server Response
        if(Objects.equals(serverResponse, "OK")){
            return new Response(HttpStatus.OK, ContentType.JSON, "[The stats could be retrieved successfully.]");
        }
        else{
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Access token is missing or invalid");

        }
    }


    public Response showScoreboardPerRepository() {
        UnitOfWork unitOfWork = new UnitOfWork();
        String serverResponse = "Failure";

        try (unitOfWork){
            //create Repository and shows the user's stats
            System.out.println("************************************* Scoreboard **********************************");

            serverResponse = new StatRepositoryImpl(unitOfWork).showScoreboard();

            System.out.println("***********************************************************************************");

            //commit changed db entry
            unitOfWork.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();

            throw new RuntimeException(e);
        }

        //Server Response
        if(Objects.equals(serverResponse, "OK")){
            return new Response(HttpStatus.OK, ContentType.JSON, "[The scoreboard could be retrieved successfully.]");
        }
        else{
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Access token is missing or invalid");

        }
    }
}
