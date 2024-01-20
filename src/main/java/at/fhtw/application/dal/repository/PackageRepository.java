package at.fhtw.application.dal.repository;

public interface PackageRepository {

    //create package
    boolean checkAdminToken(String loginToken);
    String fillPackage(String[] cardIDs);
    String createCard(String id, String name, double damage);

    //buy package
    boolean checkCredibility(String loginToken);
    void finishTransaction(String loginToken, int costs);
    String[] chooseFirstPackage();
    boolean deleteFirstPackage();
    String receiveCards(String loginToken, String cardID);
}
