package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.HeaderMap;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.sampleapp.service.BattleService;

import java.util.Collections;
import java.util.Objects;

public class BattleController implements RestController {

    private final BattleService battleService;

    public BattleController() {this.battleService = new BattleService();}

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
        if(request.getMethod() == Method.POST && Objects.equals(request.getServiceRoute(), "/battles")) {
            return this.battleService.createBattlePerRepository(authorize(request.getHeaderMap()));
        }


        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[Path is not implemented]"
        );
    }

}
