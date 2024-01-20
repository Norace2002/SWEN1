package at.fhtw.application.dal.repository;

public interface UserRepository {

    //User
    String createUser(String username, String password);
    String compareUser(String username, String password);

    //User Data
    String showUserdata(String username);
    String fillUserdata(String username, String name, String bio, String image);

}
