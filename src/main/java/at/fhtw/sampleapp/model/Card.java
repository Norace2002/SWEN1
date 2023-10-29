package at.fhtw.sampleapp.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;

@Getter
public class Card {
    @JsonAlias({"id"})
    private Integer id;
    @JsonAlias({"name"})
    private String name;
    @JsonAlias({"damage"})
    private Integer damage;
    @JsonAlias({"elementType"})
    private String elementType;
    @JsonAlias({"cardType"})
    private String cardType;

    // Default constructor
    public Card() {}

    public Card(Integer id, String name, Integer damage, String elementType, String cardType) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.elementType = elementType;
        this.cardType = cardType;
    }

    //Setter
    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDamage(Integer damage) {
        this.damage = damage;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

}
