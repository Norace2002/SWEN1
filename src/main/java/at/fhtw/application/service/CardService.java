package at.fhtw.application.service;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Response;
import at.fhtw.application.dal.UnitOfWork;
import at.fhtw.application.dal.repository.CardRepositoryImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

//Object Mapper stuff
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;

public class CardService extends AbstractService{

    public CardService() {}

    //extracts Data out of given Json styled string
    public String extractData(String userData, String keyword) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> userMap = objectMapper.readValue(userData, new TypeReference<Map<String, Object>>() {});

        // Extrahiere das Datenobjekt aus der Map
        Object extractedData = userMap.get(keyword);

        // Wandele die Zahl in einen String um, falls es sich um eine Zahl handelt
        if (extractedData instanceof Number) {
            return extractedData.toString();
        }

        // Rückgabe des extrahierten Datenobjekts (als String oder bereits vorhandenen String)
        return (String) extractedData;
    }


    public Response showCardsPerRepository(String username) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String serverResponse = "Failure";

        //check if user is logged in
        if(username.isEmpty()){
            System.out.println("Please log in first!");
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Access token is missing or invalid");
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
            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "The user has cards, the response contains these");
        }
        else if(Objects.equals(serverResponse, "EMPTY")){
            return new Response(HttpStatus.NO_CONTENT, ContentType.PLAIN_TEXT, "The request was fine, but the user doesn't have any cards");
        }
        else{
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Access token is missing or invalid");

        }

    }

    public Response showDeckPerRepository(String param, String username) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String serverResponse = "Failure";

        //check if user is logged in
        if(username.isEmpty()){
            System.out.println("Please log in first!");
        }

        try (unitOfWork){
            //create Repository and shows the user's battledeck
            serverResponse = new CardRepositoryImpl(unitOfWork).showDeck(param, username);

            //commit changed db entry
            unitOfWork.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();

            throw new RuntimeException(e);
        }

        //Server Response
        if(Objects.equals(serverResponse, "OK") || serverResponse.startsWith("[{")){
            if(Objects.equals(param, "format=plain")){
                System.out.println("check");
                return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "The deck has cards, the response contains these");
            }
            return new Response(HttpStatus.OK, ContentType.JSON, serverResponse);
        }
        else if(Objects.equals(serverResponse, "EMPTY")){
            return new Response(HttpStatus.NO_CONTENT, ContentType.PLAIN_TEXT, "The request was fine, but the deck doesn't have any cards");
        }
        else{
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Access token is missing or invalid");

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

            //create Repository
            CardRepositoryImpl repository = new CardRepositoryImpl(unitOfWork);

            //Set every card on indeck = false
            repository.resetDeck(username);


            //Configures the user's deck new
            serverResponse = repository.configureDeck(cardIds, username);

            //if the cardIds Array doesn't include 4 cards don't commit to db
            if(cardIds.size() == 4 && Objects.equals(serverResponse, "OK")){
                //commit changed db entry
                unitOfWork.commitTransaction();
            }


        } catch (Exception e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();

            throw new RuntimeException(e);
        }

        //Server Response
        if(Objects.equals(serverResponse, "OK")){
            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "The deck has been successfully configured");
        }
        else if(Objects.equals(serverResponse, "wrongAmount")){
            return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "The provided deck did not include the required amount of cards");
        }
        else if(Objects.equals(serverResponse, "cardFailure")){
            return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, "At least one of the provided cards does not belong to the user or is not available.");
        }
        else{
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Access token is missing or invalid");

        }

    }


    public Response showTradesPerRepository(String username) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String serverResponse = "Failure";

        //check if user is logged in
        if(username.isEmpty()){
            System.out.println("Please log in first!");
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Access token is missing or invalid");
        }


        try (unitOfWork){
            //create Repository shows all Cards from a specific user
            serverResponse = new CardRepositoryImpl(unitOfWork).showTrades(username);

            //commit changed db entry
            unitOfWork.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();

            throw new RuntimeException(e);
        }

        //Server Response
        if(Objects.equals(serverResponse, "OK")){
            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "There are trading deals available, the response contains these");
        }
        else{
            return new Response(HttpStatus.NO_CONTENT, ContentType.PLAIN_TEXT, "The request was fine, but there are no trading deals available");
        }
    }

    public Response tradeCardPerRepository(String tradeData, String username) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String serverResponse = "FAILURE";

        //check if user is logged in
        if(username.isEmpty()){
            System.out.println("Please log in first!");
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Access token is missing or invalid");
        }

        try (unitOfWork){
            //create Repository
            CardRepositoryImpl repository = new CardRepositoryImpl(unitOfWork);

            //Check if card is already used for a trade
            serverResponse = repository.checkDoubleTrades(extractData(tradeData, "Id"));
            if(!Objects.equals(serverResponse, "DOUBLE")){
                //If everything is fine try to create trade and check if user is allowed to trade the specific card
                serverResponse = repository.createTrade(username, extractData(tradeData, "Id"), extractData(tradeData, "CardToTrade"), extractData(tradeData, "Type"), extractData(tradeData, "MinimumDamage"));
            }

            //commit changed db entry
            unitOfWork.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();

            throw new RuntimeException(e);
        }

        //Server Response
        if(Objects.equals(serverResponse, "OK")){
            return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "Trading deal successfully created");
        }
        else if(Objects.equals(serverResponse, "FORBIDDEN")){
            return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, "The deal contains a card that is not owned by the user or locked in the deck.");
        }
        else{
            return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, "A deal with this deal ID already exists.");
        }
    }


    public Response deleteTradePerRepository(String username, String ttid) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String serverResponse = "Failure";

        //check if user is logged in
        if(username.isEmpty()){
            System.out.println("Please log in first!");
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Access token is missing or invalid");
        }


        try (unitOfWork){
            //create Repository
            CardRepositoryImpl repository = new CardRepositoryImpl(unitOfWork);

            //Check if deal id exists
            serverResponse = repository.checkDoubleTrades(ttid);
            if(Objects.equals(serverResponse, "DOUBLE")){
                //if deal id exists - delete deal
                serverResponse = repository.deleteTrade(username, ttid);
            }

            //commit changed db entry
            unitOfWork.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();

            throw new RuntimeException(e);
        }

        //Server Response
        if(Objects.equals(serverResponse, "OK")){
            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "Trading deal successfully deleted");
        }
        else if(Objects.equals(serverResponse, "FORBIDDEN")){
            return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, "The deal contains a card that is not owned by the user.");
        }
        else{
            return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "The provided deal ID was not found.");
        }
    }


    public Response acceptTradePerRepository(String username, String ttid, String tradingCardID) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String serverResponse = "FORBIDDEN";

        //Delete the "" from the given String
        tradingCardID = tradingCardID.replace("\"", "");

        //check if user is logged in
        if(username.isEmpty()){
            System.out.println("Please log in first!");
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Access token is missing or invalid");
        }

        try (unitOfWork){
            //create Repository
            CardRepositoryImpl repository = new CardRepositoryImpl(unitOfWork);
            System.out.println("~~~~~~~~~~~~~ Trading ~~~~~~~~~~~~~~");

            //Check if deal id exists
            serverResponse = repository.checkDoubleTrades(ttid);
            if(Objects.equals(serverResponse, "DOUBLE")){
                //if deal id exists - accept given deal

                //tradeInfos provide data of the username and teh ccid
                List<String> tradeInfos = repository.acceptTrade(username);

                if(!Objects.equals(tradeInfos.get(0), "FORBIDDEN") && !username.equals(tradeInfos.get(0))){
                    //Change owners
                    //First change ownership from card provided from trade - it is stored in tradeInfos
                    System.out.println("User der karte kauft: " + username);
                    System.out.println("Karte von Hochsteller: " + tradeInfos.get(1));
                    System.out.println("Hochsteller: " + tradeInfos.get(0));

                    repository.changeOwner(username, tradeInfos.get(1), tradeInfos.get(0));
                    //Now, ownership of the desired card is transferred to the original trader

                    System.out.println("Hochsteller: " + tradeInfos.get(0));
                    System.out.println("Karte von Käufer: " + tradingCardID);
                    System.out.println("User der karte kauft: " + username);
                    repository.changeOwner(tradeInfos.get(0), tradingCardID, username);
                    //finally delete trading offer
                    serverResponse = repository.deleteTrade(tradeInfos.get(0), ttid);
                }
                else{
                    System.out.println("This user can't accept the trade");
                }

            }
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

            //commit changed db entry
            unitOfWork.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();

            throw new RuntimeException(e);
        }

        //Server Response
        if(Objects.equals(serverResponse, "OK")){
            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "Trading deal successfully executed.");
        }
        else if(Objects.equals(serverResponse, "FORBIDDEN")){
            return new Response(HttpStatus.FORBIDDEN, ContentType.PLAIN_TEXT, "The offered card is not owned by the user, or the requirements are not met (Type, MinimumDamage), or the offered card is locked in the deck.");
        }
        else if(!Objects.equals(serverResponse, "DOUBLE")){
            return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "The provided deal ID was not found.");
        }
        else{
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Access token is missing or invalid");
        }
    }



}
