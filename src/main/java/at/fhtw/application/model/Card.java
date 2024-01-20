package at.fhtw.application.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;

import java.util.Objects;

@Getter
public class Card {
    @JsonAlias({"id"})
    private String id;
    @JsonAlias({"name"})
    private String name;
    @JsonAlias({"damage"})
    private Double damage;
    @JsonAlias({"elementType"})
    private String elementType;
    @JsonAlias({"cardType"})
    private String cardType;

    // Default constructor
    public Card() {}

    public Card(String id, String name, double damage, String elementType, String cardType) {
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.elementType = elementType;
        this.cardType = cardType;
    }

    //Setter
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        //declare card name
        this.name = name;


        //Extract Card type and element type
        if (Objects.equals(name, "Dragon")){
            setElementType("Fire");
        }
        else if(Objects.equals(name, "Knight") || Objects.equals(name, "Ork") || Objects.equals(name, "Wizard") || Objects.equals(name, "Narr")){
            setElementType("Regular");
        }
        else if(Objects.equals(name, "Kraken")){
            setElementType("Water");
        }
        else{
            //Regular expression is used to match the first uppercase letter
            String regex = "(?=[A-Z])";

            //We split the input string using the regex
            String[] parts = name.split(regex, 2);
            setElementType(parts[0]);
        }
    }

    public void setDamage(Double damage) {
        this.damage = damage;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public double getElementDamage(String enemyElement){
        if((Objects.equals(this.elementType, "Water") && Objects.equals(enemyElement, "Fire")) ||
                (Objects.equals(this.elementType, "Fire") && Objects.equals(enemyElement, "Regular")) ||
                (Objects.equals(this.elementType, "Regular") && Objects.equals(enemyElement, "Water"))){
            return damage*2;
        }
        else if((Objects.equals(this.elementType, "Fire") && Objects.equals(enemyElement, "Water")) ||
                (Objects.equals(this.elementType, "Regular") && Objects.equals(enemyElement, "Fire")) ||
                (Objects.equals(this.elementType, "Water") && Objects.equals(enemyElement, "Regular"))){
            return damage*0.5;
        }
        else{
            return damage;
        }
    }

}
