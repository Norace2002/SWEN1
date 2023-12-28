package at.fhtw.sampleapp.controller;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.HeaderMap;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.RestController;
import at.fhtw.sampleapp.service.PackageService;

import java.util.Objects;

public class PackageController implements RestController {
    private final PackageService packageService;

    public PackageController() {
        this.packageService = new PackageService();
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

        if (request.getMethod() == Method.POST && Objects.equals(request.getServiceRoute(), "/packages")) {
            return this.packageService.createPackagePerRepository(request.getBody(), authorize(request.getHeaderMap()));
        }
        else if(request.getMethod() == Method.POST && Objects.equals(request.getServiceRoute(), "/transactions/packages")){
            return this.packageService.buyPackagePerRepository(authorize(request.getHeaderMap()));
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[BAD REQUEST]"
        );
    }
}
