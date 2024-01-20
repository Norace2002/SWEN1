package at.fhtw.application.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.HeaderMap;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.application.service.UserService;

import java.util.Objects;

public class UserController implements RestController {
    private final UserService userService;

    public UserController() {this.userService = new UserService();}

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



        if(request.getMethod() == Method.POST && Objects.equals(request.getServiceRoute(), "/users")) {
            return this.userService.createUserPerRepository(request.getBody());
        }
        else if(request.getMethod() == Method.GET && request.getPathParts().size() == 2 && Objects.equals(request.getPathParts().get(0), "users")) {
            return this.userService.showUserDataPerRepository(authorize(request.getHeaderMap()), request.getPathParts().get(1));
        }
        else if(request.getMethod() == Method.PUT && request.getPathParts().size() == 2 && Objects.equals(request.getPathParts().get(0), "users")) {
            return this.userService.fillUserDataPerRepository(authorize(request.getHeaderMap()), request.getPathParts().get(1), request.getBody());
        }
        else if(request.getMethod() == Method.POST && Objects.equals(request.getServiceRoute(), "/sessions")) {
            return this.userService.compareUserToDatabase(request.getBody());
        }


        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[Path is not implemented]"
        );
    }
}
