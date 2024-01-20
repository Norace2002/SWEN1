package at.fhtw.application.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.HeaderMap;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.application.service.CardService;

import java.util.Objects;

public class CardController  implements RestController {

    private final CardService cardService;

    public CardController() {this.cardService = new CardService();}

    private String authorize(HeaderMap headerMap){
        String token = "";

        // Extract username from Header
        String authorizationHeader = headerMap.getHeader("Authorization");

        // Check if the header is empty or null
        if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
            // Return an empty string or handle it based on your requirements
            return "";
        }

        // Get rid of the "Bearer " Part and return the token
        token = authorizationHeader.replace("Bearer ", "");

        //split the token in two parts
        String[] parts = token.split("-");

        //extract username out of token
        return parts[0];
    }

    @Override
    public Response handleRequest(Request request) {

        if(request.getMethod() == Method.GET && Objects.equals(request.getServiceRoute(), "/cards")){
            return this.cardService.showCardsPerRepository(authorize(request.getHeaderMap()));
        }
        else if(request.getMethod() == Method.GET && Objects.equals(request.getServiceRoute(), "/deck")){
            return this.cardService.showDeckPerRepository(request.getParams(), authorize(request.getHeaderMap()));
        }
        else if(request.getMethod() == Method.PUT && Objects.equals(request.getServiceRoute(), "/deck")) {
            return this.cardService.chooseDeckPerRepository(request.getBody(), authorize(request.getHeaderMap()));
        }
        else if(request.getMethod() == Method.GET && Objects.equals(request.getServiceRoute(), "/tradings")) {
            return this.cardService.showTradesPerRepository(authorize(request.getHeaderMap()));
        }
        else if(request.getMethod() == Method.POST && Objects.equals(request.getServiceRoute(), "/tradings")) {
            return this.cardService.tradeCardPerRepository(request.getBody(), authorize(request.getHeaderMap()));
        }
        else if(request.getMethod() == Method.DELETE && request.getPathParts().size() == 2 && Objects.equals(request.getPathParts().get(0), "tradings")) {
            return this.cardService.deleteTradePerRepository(authorize(request.getHeaderMap()), request.getPathParts().get(1));
        }
        else if(request.getMethod() == Method.POST && request.getPathParts().size() == 2 && Objects.equals(request.getPathParts().get(0), "tradings")) {
            return this.cardService.acceptTradePerRepository(authorize(request.getHeaderMap()), request.getPathParts().get(1), request.getBody());
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[Path is not implemented]"
        );
    }
}
