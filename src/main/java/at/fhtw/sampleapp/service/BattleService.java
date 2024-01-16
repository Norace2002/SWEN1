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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    //The ConcurrentHashMap takes username as input if first user starts battle / second user checks HashMap for other user and starts battle
    private Map<String,User> playerInfoMap = new ConcurrentHashMap<>();

    public Response createBattlePerRepository(String username) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String serverResponse = "FAILURE";
        List<String> battleInfo = new ArrayList<>();

        try (unitOfWork){
            if(Objects.equals(username, "")){
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Access token is missing or invalid");

            }


            //create Repository search for cards in deck
            List<Card> deck = new BattleRepositoryImpl(unitOfWork).getDeck(username);
            User playerInfo = new User(username, deck);

            if(deck.size() == 4){
                //if everything is okay, fill playerInfoMap
                playerInfoMap.put(username, playerInfo);

                synchronized (playerInfo) {
                    if (playerInfoMap.size() > 1) {
                        for (Map.Entry<String, User> entry : playerInfoMap.entrySet()) {
                            if (!entry.getKey().equals(username)) {
                                String opponent = entry.getKey();
                                User opponentInfo = entry.getValue();

                                playerInfoMap.remove(username);
                                playerInfoMap.remove(opponent);

                                // Here the battle starts between the 2 users
                                System.out.println("--- Battle started between " + username + " and " + opponent + " ---");

                                //--- Carry battle out ---
                                BattleLogic battleLogic = new BattleLogic(playerInfo, opponentInfo);
                                battleInfo = battleLogic.start();
                                notifyPlayerArrival(username);

                                if(!Objects.equals(battleInfo.get(0), "DRAW")){
                                    //adjust eloscore
                                    //add 3 points to winner
                                    serverResponse = new BattleRepositoryImpl(unitOfWork).adjustELO(battleInfo.get(0), 3);

                                    if(Objects.equals(serverResponse, "OK")){
                                        //subtract 5 points from loser
                                        serverResponse = new BattleRepositoryImpl(unitOfWork).adjustELO(battleInfo.get(1), -5);
                                    }
                                }else{
                                    System.out.println("Due to the Draw - The eloscore remains unchanged");
                                    serverResponse = "OK";
                                }

                                //Send server response
                                if(Objects.equals(serverResponse, "OK")){
                                    return new Response(HttpStatus.OK, ContentType.JSON, "[The battle has been carried out successfully.]");
                                }
                                else{
                                    return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Access token is missing or invalid");
                                }
                            }
                        }
                    }

                    // No other player is waiting yet
                    System.out.println("Waiting for the second player ... (" + username + " is waiting)");
                    try {
                        playerInfo.wait(); // Wait until another player notifies
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }






            /*/--------------------Tests------------------------------
            List<Card> deck1 = new BattleRepositoryImpl(unitOfWork).getDeck("kienboec");
            List<Card> deck2 = new BattleRepositoryImpl(unitOfWork).getDeck("altenhof");

            if(deck1.size() == 4 && deck2.size() == 4){
                User user1 = new User("kienboec", deck1);
                User user2 = new User("altenhof", deck2);
                //Notiz: der Thread ist jetzt bereit und soll auf seinen Gegner warten
                //danach beginnt das battle
                BattleLogic battleLogic = new BattleLogic(user1, user2);
                battleInfo = battleLogic.start();

                if(!Objects.equals(battleInfo.get(0), "DRAW")){
                    //adjust eloscore
                    //add 3 points to winner
                    serverResponse = new BattleRepositoryImpl(unitOfWork).adjustELO(battleInfo.get(0), 3);

                    if(Objects.equals(serverResponse, "OK")){
                        //subtract 5 points from loser
                        serverResponse = new BattleRepositoryImpl(unitOfWork).adjustELO(battleInfo.get(1), -5);
                    }
                }else{
                    System.out.println("Due to the Draw - The eloscore remains unchanged");
                    serverResponse = "OK";
                }
            }
            //-------------------------------------------------------*/
            




            //commit changed db entry
            unitOfWork.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();

            throw new RuntimeException(e);
        }

        //Server Response if battle fails
        return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "['#/components/responses/UnauthorizedError']");

    }



    public void notifyPlayerArrival(String username) {
        User playerInfo = playerInfoMap.get(username);
        if (playerInfo != null) {
            synchronized (playerInfo) {
                playerInfo.notify(); // Notify the waiting player
            }
        }
    }

}
