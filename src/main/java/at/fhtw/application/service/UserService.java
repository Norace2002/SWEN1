package at.fhtw.application.service;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Response;
import at.fhtw.application.dal.UnitOfWork;
import at.fhtw.application.dal.repository.UserRepositoryImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

//Object Mapper stuff
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;

public class UserService extends AbstractService {

    public UserService() {}

    //extracts Data out of given Json styled string
    public String extractData(String userData, String keyword) throws JsonProcessingException {
        String extractedData = "";

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> userMap = objectMapper.readValue(userData, new TypeReference<Map<String, Object>>() {});

        //Extract username/ password out of map object
        extractedData = (String) userMap.get(keyword);

        return extractedData;
    }

    // POST /user
    public Response createUserPerRepository(String userData) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String serverResponse = "FAILURE";
        try (unitOfWork){
            //create Repository and create User if not already exists
            serverResponse = new UserRepositoryImpl(unitOfWork).createUser(extractData(userData, "Username"), extractData(userData, "Password"));

            //commit changed db entry
            unitOfWork.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();

            throw new RuntimeException(e);
        }

        //Server Response
        if(Objects.equals(serverResponse, "OK")){
            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "User successfully created");
        }
        else{
            return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, "User with same username already registered");
        }

    }


    public Response compareUserToDatabase(String userData) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String serverResponse = "FAILURE";
        try (unitOfWork){
            //create Repository and create User if not already exists
            serverResponse = new UserRepositoryImpl(unitOfWork).compareUser(extractData(userData, "Username"), extractData(userData, "Password"));

            //commit changed db entry
            unitOfWork.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();

            throw new RuntimeException(e);
        }

        //Server Response
        if(Objects.equals(serverResponse, "OK")){
            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "User login successful");
        }
        else{
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Invalid username/password provided");
        }
    }


    public Response showUserDataPerRepository(String token, String username) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String serverResponse = "Failure";

        //check if user is logged in
        if(username.isEmpty() || !Objects.equals(token, username)){
            System.out.println("Unauthorized! Please log in first!");
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Access token is missing or invalid");
        }

        try (unitOfWork){
            //create Repository and shows the user's stats
            serverResponse = new UserRepositoryImpl(unitOfWork).showUserdata(username);

            //commit changed db entry
            unitOfWork.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();

            throw new RuntimeException(e);
        }

        //Server Response
        if(Objects.equals(serverResponse, "OK")){
            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "Data successfully retrieved");
        }
        else if(Objects.equals(serverResponse, "NotFound")){
            return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "User not found.");
        }
        else{
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Access token is missing or invalid");

        }
    }

    public Response fillUserDataPerRepository(String token, String username, String userData) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String serverResponse = "Failure";

        //check if user is logged in
        if(username.isEmpty() || !Objects.equals(token, username)){
            System.out.println("Unauthorized! Please log in first!");
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Access token is missing or invalid");
        }

        try (unitOfWork){
            //create Repository and shows the user's stats
            serverResponse = new UserRepositoryImpl(unitOfWork).fillUserdata(username, extractData(userData, "Name"), extractData(userData, "Bio"), extractData(userData, "Image"));

            //commit changed db entry
            unitOfWork.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();

            throw new RuntimeException(e);
        }

        //Server Response
        if(Objects.equals(serverResponse, "OK")){
            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "User successfully updated.");
        }
        else if(Objects.equals(serverResponse, "NotFound")){
            return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "User not found.");
        }
        else{
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Access token is missing or invalid");

        }
    }

}
