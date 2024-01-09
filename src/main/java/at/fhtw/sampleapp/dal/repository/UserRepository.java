package at.fhtw.sampleapp.dal.repository;

import at.fhtw.sampleapp.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserRepository {

    //User
    String createUser(String username, String password);
    String compareUser(String username, String password);

    //User Data
    String showUserdata(String username);
    String fillUserdata(String username, String name, String bio, String image);

}
