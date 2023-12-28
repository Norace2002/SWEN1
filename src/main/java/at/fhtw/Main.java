package at.fhtw;

import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Server;
import at.fhtw.httpserver.utils.Router;
import at.fhtw.sampleapp.controller.UserController;
import at.fhtw.sampleapp.service.UserService;
import at.fhtw.sampleapp.controller.PackageController;
import at.fhtw.sampleapp.service.PackageService;


import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        Server server = new Server(10001, configureRouter());
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private static Router configureRouter()
    {
        Router router = new Router();
        router.addService("/users", new UserController());
        router.addService("/sessions", new UserController());
        router.addService("/packages", new PackageController());
        router.addService("/transactions/packages", new PackageController());
        router.addService("/cards", new UserController());
        router.addService("/deck", new UserController());


        return router;
    }
}
