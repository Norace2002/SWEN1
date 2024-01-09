package at.fhtw.httpserver.utils;

import at.fhtw.httpserver.server.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Router {
    private Map<String, RestController> serviceRegistry = new HashMap<>();

    public void addService(String route, RestController service)
    {
        this.serviceRegistry.put(route, service);
    }

    public void removeService(String route)
    {
        this.serviceRegistry.remove(route);
    }

    public RestController resolve(String route) {


        for (Map.Entry<String, RestController> entry : serviceRegistry.entrySet()) {
            String registeredRoute = entry.getKey();
            RestController controller = entry.getValue();

            if (matchesRoute(registeredRoute, route)) {
                return controller;
            }
        }
        return null;



        //return this.serviceRegistry.get(route);
    }

    //To handle /users/* (wildcard)
    private boolean matchesRoute(String registeredRoute, String requestedRoute) {
        Pattern pattern = Pattern.compile(registeredRoute.replaceAll("\\{[^/]+}", "[^/]+"));
        Matcher matcher = pattern.matcher(requestedRoute);

        return matcher.matches();
    }

}
