package at.fhtw.sampleapp.service;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.model.User;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.dal.repository.UserRepository;
import at.fhtw.sampleapp.dal.repository.UserRepositoryImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

//Object Mapper stuff
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.core.type.TypeReference;

public class UserService extends AbstractService {

    public UserService() {
        //this.userRepository = new UserRepositoryImpl(new UnitOfWork());
    }

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
            return new Response(HttpStatus.OK, ContentType.JSON, "[User successfully created]");
        }
        else{
            return new Response(HttpStatus.CONFLICT, ContentType.JSON, "[User with same username already registered]");
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
            return new Response(HttpStatus.OK, ContentType.JSON, "[User login successful]");
        }
        else{
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "[Invalid username/password provided]");
        }
    }

    public Response showCardsPerRepository(String loginToken) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String serverResponse = "Failure";

        try (unitOfWork){
            //create Repository and create User if not already exists
            serverResponse = new UserRepositoryImpl(unitOfWork).showCards(loginToken);

            //commit changed db entry
            unitOfWork.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();

            throw new RuntimeException(e);
        }

        //Server Response
        if(Objects.equals(serverResponse, "OK")){
            return new Response(HttpStatus.OK, ContentType.JSON, "[The user has cards, the response contains these]");
        }
        else if(Objects.equals(serverResponse, "EMPTY")){
            return new Response(HttpStatus.NO_CONTENT, ContentType.JSON, "[The request was fine, but the user doesn't have any cards]");
        }
        else{
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "['#/components/responses/UnauthorizedError']");

        }

    }



}
