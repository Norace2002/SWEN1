package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.HeaderMap;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.sampleapp.service.UserService;

import java.util.Objects;

public class UserController implements RestController {
    private final UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    private String authorize(HeaderMap headerMap){

        // Extract the Header
        String authorizationHeader = headerMap.getHeader("Authorization");

        // Check if the header is empty or null
        if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
            // Return an empty string or handle it based on your requirements
            return "";
        }

        // Get rid of the "Bearer " Part and return the token
        return authorizationHeader.replace("Bearer ", "");
    }

    @Override
    public Response handleRequest(Request request) {

        if(request.getMethod() == Method.POST && Objects.equals(request.getServiceRoute(), "/users")) {
            return this.userService.createUserPerRepository(request.getBody());
        }
        else if(request.getMethod() == Method.POST && Objects.equals(request.getServiceRoute(), "/sessions")) {
            return this.userService.compareUserToDatabase(request.getBody());
        }
        else if(request.getMethod() == Method.GET && Objects.equals(request.getServiceRoute(), "/cards")){

            return this.userService.showCardsPerRepository(authorize(request.getHeaderMap()));
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[Path is not implemented]"
        );
    }
}
