package at.fhtw.sampleapp.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;

@Getter
public class Package {
    @JsonAlias({"id"})
    private String id;
    @JsonAlias({"cards"})
    private Card[] cards ;


    //Default Constructor
    public Package(){

    }

    public Package(String id, Card[] cards) {
        this.id = id;
        this.cards = cards; // 4 Cards in one package
    }

    //Setter
    public void setId(String id) {
        this.id = id;
    }

    public void setCards(Card[] cards) {
        this.cards = cards;
    }

}
