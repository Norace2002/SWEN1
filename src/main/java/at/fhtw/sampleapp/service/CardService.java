package at.fhtw.sampleapp.service;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.dal.repository.CardRepositoryImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

//Object Mapper stuff
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;

public class CardService extends AbstractService{

    public CardService() {}

    public Response showCardsPerRepository(String username) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String serverResponse = "Failure";

        //check if user is logged in
        if(username.isEmpty()){
            System.out.println("Please log in first!");
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "['#/components/responses/UnauthorizedError']");
        }


        try (unitOfWork){
            //create Repository shows all Cards from a specific user
            serverResponse = new CardRepositoryImpl(unitOfWork).showCards(username);

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

    public Response showDeckPerRepository(String param, String username) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String serverResponse = "Failure";
        System.out.println(param);

        //check if user is logged in
        if(username.isEmpty()){
            System.out.println("Please log in first!");
        }

        try (unitOfWork){
            //create Repository and shows the user's battledeck
            serverResponse = new CardRepositoryImpl(unitOfWork).showDeck(username);

            //commit changed db entry
            unitOfWork.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();

            throw new RuntimeException(e);
        }

        //Server Response
        if(Objects.equals(serverResponse, "OK")){
            if(Objects.equals(param, "format=plain")){
                return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "[The deck has cards, the response contains these]");
            }
            return new Response(HttpStatus.OK, ContentType.JSON, "[The deck has cards, the response contains these]");
        }
        else if(Objects.equals(serverResponse, "EMPTY")){
            return new Response(HttpStatus.NO_CONTENT, ContentType.JSON, "[The request was fine, but the deck doesn't have any cards]");
        }
        else{
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "['#/components/responses/UnauthorizedError']");

        }
    }

    public Response chooseDeckPerRepository(String deckString, String username) {
        UnitOfWork unitOfWork = new UnitOfWork();
        List<String> cardIds = new ArrayList<>();
        String serverResponse = "Failure";

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Convert JSON string to List of Strings
            cardIds = objectMapper.readValue(deckString, new TypeReference<List<String>>() {});

        } catch (Exception e) {
            e.printStackTrace();
        }

        //check if user is logged in
        if(username.isEmpty()){
            System.out.println("Please log in first!");
        }

        try (unitOfWork){
            //create Repository and configures the user's deck
            serverResponse = new CardRepositoryImpl(unitOfWork).configureDeck(cardIds, username);

            //commit changed db entry
            unitOfWork.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();

            throw new RuntimeException(e);
        }

        //Server Response
        if(Objects.equals(serverResponse, "OK")){
            return new Response(HttpStatus.OK, ContentType.JSON, "[The deck has been successfully configured]");
        }
        else if(Objects.equals(serverResponse, "wrongAmount")){
            return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "[The provided deck did not include the required amount of cards]");
        }
        else if(Objects.equals(serverResponse, "cardFailure")){
            return new Response(HttpStatus.FORBIDDEN, ContentType.JSON, "[At least one of the provided cards does not belong to the user or is not available.]");
        }
        else{
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "['#/components/responses/UnauthorizedError']");

        }

    }

}
