package at.fhtw;

import at.fhtw.httpserver.server.Server;
import at.fhtw.httpserver.utils.Router;
import at.fhtw.application.controller.UserController;
import at.fhtw.application.controller.PackageController;
import at.fhtw.application.controller.CardController;
import at.fhtw.application.controller.StatController;
import at.fhtw.application.controller.BattleController;



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
        //User Services
        router.addService("/users", new UserController());
        router.addService("/sessions", new UserController());
        router.addService("/users/{username}", new UserController());

        //Package Services
        router.addService("/packages", new PackageController());
        router.addService("/transactions/packages", new PackageController());

        //Card Services
        router.addService("/cards", new CardController());
        router.addService("/deck", new CardController());
        router.addService("/tradings", new CardController());
        router.addService("/tradings/{ttid}", new CardController());

        //Stat Services
        router.addService("/stats", new StatController());
        router.addService("/scoreboard", new StatController());

        //Battle Services
        router.addService("/battles", new BattleController());




        return router;
    }
}
