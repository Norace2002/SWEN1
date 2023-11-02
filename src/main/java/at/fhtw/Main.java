package at.fhtw;

import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Server;
import at.fhtw.httpserver.utils.Router;
import at.fhtw.sampleapp.controller.UserController;
import at.fhtw.sampleapp.service.UserService;


import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        Server server = new Server(10001, configureRouter());
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*UserService service = new UserService();
        Request request = new Request();
        request.setMethod(Method.POST);
        System.out.println(service.handleRequest(request));*/
    }
    private static Router configureRouter()
    {
        Router router = new Router();
        router.addService("/users", new UserController());


        return router;
    }
}
