package at.fhtw.sampleapp.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@Getter
public class User {
    @JsonAlias({"username"})
    private String username;
    @JsonAlias({"deck"})
    private List<Card> deck;


    //Default Constructor
    public User(){}

    public User(String username, List<Card> deck) {
        this.username = username;
        this.deck = deck;
    }

    //Setter
    public void setUsername(String username) {
        this.username = username;
    }

    public void setDeck(List<Card> deck) {
        this.deck = deck;
    }


    //Game functions
    public int randomNumberGenerator(int max){
        // Creates a random object
        Random random = new Random();

        //Generates a Number between 0 and given variable-1
        return random.nextInt(max);
    }

    public boolean gameOver(){
        return deck.isEmpty();
    }

    public Card chooseCard(){
        return deck.get(randomNumberGenerator(deck.size()));
    }

    public void addCard(Card card){
        deck.add(card);
        System.out.println(username + ": " + deck.size() + " cards left");
    }

    public void loseCard(Card card){
        for(int i = 0; i < deck.size(); ++i){
            if(Objects.equals(card.getId(), deck.get(i).getId())){
                deck.remove(i);
                break;
            }
        }
        System.out.println(username + ": " + deck.size() + " cards left");
    }

}
