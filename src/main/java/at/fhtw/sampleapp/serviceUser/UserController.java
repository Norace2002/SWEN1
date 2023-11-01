package at.fhtw.sampleapp.serviceUser;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.controller.Controller;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.dal.repository.UserRepository;
import at.fhtw.sampleapp.model.User;

import java.util.Collection;
import java.util.List;

public class UserController extends Controller {
    // POST /users

    public Response createUserPerRepository() {
        UnitOfWork unitOfWork = new UnitOfWork();
        try (unitOfWork){
            // "{\"Username\":\"kienboec\", \"Password\":\"daniel\"}"
            new UserRepository(unitOfWork).createUser();

            unitOfWork.commitTransaction();
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "[]"
            );
        } catch (Exception e) {
            e.printStackTrace();

            unitOfWork.rollbackTransaction();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }
}
