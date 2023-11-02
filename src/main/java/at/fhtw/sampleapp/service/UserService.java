package at.fhtw.sampleapp.service;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.model.User;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.dal.repository.UserRepository;
import at.fhtw.sampleapp.dal.repository.UserRepositoryImpl;
import com.fasterxml.jackson.core.JsonProcessingException;

//Object Mapper stuff
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;

public class UserService extends AbstractService {
    //private UserRepository userRepository;

    public UserService() {
        //this.userRepository = new UserRepositoryImpl(new UnitOfWork());
    }

    // POST /user
    public Response createUserPerRepository(String userData) {
        UnitOfWork unitOfWork = new UnitOfWork();
        try (unitOfWork){
            ObjectMapper objectMapper = new ObjectMapper();

            Map<String, Object> userMap = objectMapper.readValue(userData, new TypeReference<Map<String, Object>>() {});

            //Extract username/ password out of map object
            String username = (String) userMap.get("Username");
            String password = (String) userMap.get("Password");
            System.out.println("Username: " + username + ", PW: " + password);

            //create Repository and create User if not already exists
            new UserRepositoryImpl(unitOfWork).createUser(username, password);

            //commit changed db entry
            unitOfWork.commitTransaction();

        } catch (Exception e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();

            throw new RuntimeException(e);
        }
        return new Response(HttpStatus.OK, ContentType.JSON, "[]");
    }

}
