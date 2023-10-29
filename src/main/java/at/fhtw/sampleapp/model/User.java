package at.fhtw.sampleapp.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;

@Getter
public class User {
    @JsonAlias({"username"})
    private String username;
    @JsonAlias({"password"})
    private String password;
    @JsonAlias({"coins"})
    private Integer coins;
    @JsonAlias({"stack"})
    private Card[] stack;
    @JsonAlias({"stack"})
    private Integer eloScore;




    //Default Constructor
    public User(){}

    public User(String username, String password, Card[] stack) {
        this.username = username;
        this.password = password;
        this.coins = 20;
        this.stack = stack;
        this.eloScore = 100;
    }

    //Setter
    public void setId(String username) {
        this.username = username;
    }

    public void setName(String password) {
        this.password = password;
    }

    public void setDamage(Integer coins) {
        this.coins = coins;
    }

    public void setElementType(Card[] stack) {
        this.stack = stack;
    }

    public void setCardType(Integer eloScore) {
        this.eloScore = eloScore;
    }
}
