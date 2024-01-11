package at.fhtw.sampleapp.service;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.dal.repository.BattleRepositoryImpl;
import at.fhtw.sampleapp.dal.repository.UserRepositoryImpl;
import at.fhtw.sampleapp.model.Card;
import at.fhtw.sampleapp.model.User;
import at.fhtw.sampleapp.model.BattleLogic;
import com.fasterxml.jackson.core.JsonProcessingException;

//Object Mapper stuff
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.ResultSet;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;

public class BattleService {
    public BattleService() {}

    //extracts Data out of given Json styled string
    public String extractData(String userData, String keyword) throws JsonProcessingException {
        String extractedData = "";

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> userMap = objectMapper.readValue(userData, new TypeReference<Map<String, Object>>() {});

        //Extract username/ password out of map object
        extractedData = (String) userMap.get(keyword);

        return extractedData;
    }

    public Response createBattlePerRepository(String username) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String serverResponse = "FAILURE";
        try (unitOfWork){
            //create Repository search for cards in deck
            /*List<Card> deck = new BattleRepositoryImpl(unitOfWork).getDeck(username);

            if(deck.size() == 4){
                User user = new User(username, deck);
                //Notiz: der Thread ist jetzt bereit und soll auf seinen Gegner warten
                //danach beginnt das battle
                BattleLogic battleLogic = new BattleLogic();

                serverResponse = "OK";
            }*/




            //--------------------Tests------------------------------
            List<Card> deck1 = new BattleRepositoryImpl(unitOfWork).getDeck("kienboec");
            List<Card> deck2 = new BattleRepositoryImpl(unitOfWork).getDeck("altenhof");

            if(deck1.size() == 4 && deck2.size() == 4){
                User user1 = new User("kienboec", deck1);
                User user2 = new User("altenhof", deck2);
                //Notiz: der Thread ist jetzt bereit und soll auf seinen Gegner warten
                //danach beginnt das battle
                BattleLogic battleLogic = new BattleLogic(user1, user2);
                battleLogic.start();

                serverResponse = "OK";
            }
            //-------------------------------------------------------
            




            //commit changed db entry
            unitOfWork.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();

            throw new RuntimeException(e);
        }

        //Server Response
        if(Objects.equals(serverResponse, "OK")){
            return new Response(HttpStatus.OK, ContentType.JSON, "[The battle has been carried out successfully.]");
        }
        else{
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "['#/components/responses/UnauthorizedError']");
        }

    }

}
