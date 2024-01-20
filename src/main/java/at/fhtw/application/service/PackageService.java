package at.fhtw.application.service;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Response;
import at.fhtw.application.dal.UnitOfWork;
import at.fhtw.application.dal.repository.PackageRepositoryImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

//Object Mapper stuff
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.util.Objects;

import com.fasterxml.jackson.core.type.TypeReference;


public class PackageService extends AbstractService{
    public PackageService() {
    }

    //extracts Data out of given Json styled string
    public List<Map<String, Object>> extractCards(String packageData) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Use a List to extract the data from the JSON array
            return objectMapper.readValue(
                    packageData,
                    new TypeReference<List<Map<String, Object>>>() {}
            );
        } catch (JsonProcessingException e) {
            // Handle the exception here
            System.err.println("Error processing JSON data format: " + e.getMessage());
            // Return an empty list or a specific indicator based on your needs
            return Collections.emptyList();
        }
    }

    // POST /packages
    public Response createPackagePerRepository(String packageData, String loginToken){
        UnitOfWork unitOfWork = new UnitOfWork();
        String serverResponse = "Failure";

        //create List to extract cards
        List<Map<String, Object>> cards = extractCards(packageData);
        String[] cardIDs = new String[5];
        int i = 0;

        try (unitOfWork){

            //create PackageRepository Object
            PackageRepositoryImpl repository = new PackageRepositoryImpl(unitOfWork);

            //check if admin token is set
            if(repository.checkAdminToken(loginToken)){

                //create Cards in card table
                for (Map<String, Object> card : cards) {

                    //calls a sql statement and insert cards into card table
                    //cardIDs array collects freshly created card ids to insert them into package
                    cardIDs[i++] = repository.createCard((String) card.get("Id"), (String) card.get("Name"), (double) card.get("Damage"));
                }

                //check if there is a card that already exits n the package
                for (String card : cardIDs) {
                    if(Objects.equals(card, "DOUBLE")){
                        return new Response(HttpStatus.CONFLICT, ContentType.JSON, "[At least one card in the packages already exists]");

                    }
                }

                //fill package table with specific Card ids if there is no double in the package
                serverResponse = repository.fillPackage(cardIDs);
            }
            else{
                serverResponse = "notAdmin";
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
            return new Response(HttpStatus.CREATED, ContentType.JSON, "[Package and cards successfully created]");
        }
        else if(Objects.equals(serverResponse, "notAdmin")){
            return new Response(HttpStatus.FORBIDDEN, ContentType.JSON, "[Provided user is not \"admin\"]");
        }
        else{
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Access token is missing or invalid");
        }

    }

    public Response buyPackagePerRepository(String loginToken) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String serverResponse = "Failure";

        try (unitOfWork){
            //determine variables
            int costs = 5;
            String[] cardIDArray = new String[5];

            //create PackageRepository Object
            PackageRepositoryImpl repository = new PackageRepositoryImpl(unitOfWork);

            //check credibility of current User
            if(repository.checkCredibility(loginToken)){
                //subtract 5 coins to buy package

                //get first package in table
                cardIDArray = repository.chooseFirstPackage();

                //Check if there was a package left
                for (String cardID : cardIDArray) {
                    if(Objects.equals(cardID, null)){
                        return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "[No card package available for buying]");
                    }
                }

                //delete used up package
                if(repository.deleteFirstPackage()){
                    //Makes sure there is a package left before subtracting the coins
                    repository.finishTransaction(loginToken, costs);
                }

                for (String cardID : cardIDArray) {
                    //user receives cardIDs
                    serverResponse = repository.receiveCards(loginToken, cardID);
                    if(!Objects.equals(serverResponse, "OK")){
                        break;
                    }
                }
            }
            else{
                serverResponse = "noCoins";
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
            return new Response(HttpStatus.OK, ContentType.JSON, "[A package has been successfully bought]");
        }
        else if(Objects.equals(serverResponse, "noCoins")){
            return new Response(HttpStatus.FORBIDDEN, ContentType.JSON, "[Not enough money for buying a card package]");
        }
        else{
            return new Response(HttpStatus.UNAUTHORIZED, ContentType.JSON, "Access token is missing or invalid");
        }

    }

}
