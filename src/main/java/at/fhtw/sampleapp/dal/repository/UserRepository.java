package at.fhtw.sampleapp.dal.repository;

import at.fhtw.sampleapp.model.User;

import java.util.Collection;

public interface UserRepository {

    void createUser(String username, String password);

}
