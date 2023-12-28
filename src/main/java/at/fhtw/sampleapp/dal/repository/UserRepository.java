package at.fhtw.sampleapp.dal.repository;

import at.fhtw.sampleapp.model.User;

import java.util.Collection;

public interface UserRepository {

    String createUser(String username, String password);
    String compareUser(String username, String password);
    String showCards(String loginToken);

}
