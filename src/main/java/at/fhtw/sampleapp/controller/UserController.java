package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.sampleapp.service.UserService;

public class UserController implements RestController {
    private final UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.POST) {
            return this.userService.createUserPerRepository(request.getBody());
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
