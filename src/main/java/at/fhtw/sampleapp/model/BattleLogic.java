package at.fhtw.sampleapp.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;

import java.util.Objects;

@Getter
public class BattleLogic {
    @JsonAlias({"user1"})
    private User user1;
    @JsonAlias({"user2"})
    private User user2;


    // Default constructor
    public BattleLogic() {}

    public BattleLogic(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    //Setter
    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    //Overall functions
    public void getRewardUser1(Card card){
        user1.addCard(card);
        user2.loseCard(card);
        System.out.println(user1.getUsername() + " won this round!");
    }

    public void getRewardUser2(Card card){
        user2.addCard(card);
        user1.loseCard(card);
        System.out.println(user2.getUsername() + " won this round!");
    }

    public void executeRound(Card user1card, Card user2card){
        //We need to extract just the race for all edge cases like WaterGoblin - Goblin
        //Regular expression is used to match the first uppercase letter
        String regex = "(?=[A-Z])";

        //We split the input string using the regex
        String[] partsUser1card = user1card.getName().split(regex, 2);
        String[] partsUser2card = user2card.getName().split(regex, 2);

        System.out.println("Kienboec plays: " + user1card.getName() + " (" + user1card.getDamage() + ")");
        System.out.println("Altenhof plays: " + user2card.getName() + " (" + user2card.getDamage() + ")");

        //Check if there is a Spell. Because if not, we can ignore types
        if(!Objects.equals(user1card.getCardType(), "Spell") && !Objects.equals(user2card.getCardType(), "Spell")){

            if(partsUser1card.length == 2 && Objects.equals(partsUser1card[1], "Goblin") && Objects.equals(user2card.getName(), "Dragon")){
                System.out.println(user1.getUsername() + "'s Goblin is to afraid to fight and runs away!");
                getRewardUser2(user1card);
            }
            else if(Objects.equals(user1card.getName(), "Dragon") && partsUser2card.length == 2 && Objects.equals(partsUser2card[1], "Goblin")){
                System.out.println(user2.getUsername() + "'s Goblin is to afraid to fight and runs away!");
                getRewardUser1(user2card);
            }
            else if(Objects.equals(user1card.getName(), "Ork") && Objects.equals(user2card.getName(), "Wizzard")){
                System.out.println(user1.getUsername() + "'s Ork stabs himself due to the Wizzard's magical Power!");
                getRewardUser2(user1card);
            }
            else if(Objects.equals(user1card.getName(), "Wizzard") && Objects.equals(user2card.getName(), "Ork")){
                System.out.println(user2.getUsername() + "'s Ork stabs himself due to the Wizzard's magical Power!");
                getRewardUser1(user2card);
            }
            else if(Objects.equals(user1card.getName(), "Dragon") && Objects.equals(user2card.getName(), "FireElf")){
                System.out.println(user2.getUsername() + "'s Elf dodges every attack. The Dragon can't beat his master");
                getRewardUser2(user1card);
            }
            else if(Objects.equals(user1card.getName(), "FireElf") && Objects.equals(user2card.getName(), "Dragon")){
                System.out.println(user1.getUsername() + "'s Elf dodges every attack. The Dragon can't beat his master");
                getRewardUser1(user2card);
            }
            else if(user1card.getDamage() < user2card.getDamage()){
                System.out.println(user2.getUsername() + "'s " + user2card.getName() + " defeats (" + user2card.getDamage() + ") " +
                        user1.getUsername() + "'s " + user1card.getName() + " (" + user1card.getDamage() + ")");
                getRewardUser2(user1card);
            }
            else if(user1card.getDamage() > user2card.getDamage()){
                System.out.println(user1.getUsername() + "'s " + user1card.getName() + " defeats (" + user1card.getDamage() + ") " +
                        user2.getUsername() + "'s " + user2card.getName() + " (" + user2card.getDamage() + ")");
                getRewardUser1(user2card);
            }
            else{
                System.out.println("They both dodge each other's attacks");
            }
        }
        else{
            if(Objects.equals(user1card.getName(), "Knight") && Objects.equals(user2card.getElementType(), "Water")){
                System.out.println(user1.getUsername() + "'s Knight can't swim so he drowns immediately!");
                getRewardUser2(user1card);
            }
            else if(Objects.equals(user1card.getElementType(), "Water") && Objects.equals(user2card.getName(), "Knight")){
                System.out.println(user2.getUsername() + "'s Knight can't swim so he drowns immediately!");
                getRewardUser1(user2card);
            }
            else if(Objects.equals(user2card.getName(), "Kraken")){
                System.out.println(user2.getUsername() + "'s Kraken doesn't even notice the spell");
                getRewardUser2(user1card);
            }
            else if(Objects.equals(user1card.getName(), "Kraken")){
                System.out.println(user1.getUsername() + "'s Kraken doesn't even notice the spell");
                getRewardUser1(user2card);
            }
            else{
                if(user1card.getElementDamage(user2card.getElementType()) < user2card.getElementDamage(user1card.getElementType())){
                    System.out.println(user2.getUsername() + "'s " + user2card.getName() + " defeats (" + user2card.getElementDamage(user1card.getElementType()) + ") " +
                            user1.getUsername() + "'s " + user1card.getName() + " (" + user1card.getElementDamage(user2card.getElementType()) + ")");
                    getRewardUser2(user1card);
                }
                else if(user1card.getElementDamage(user2card.getElementType()) > user2card.getElementDamage(user1card.getElementType())){
                    System.out.println(user1.getUsername() + "'s " + user1card.getName() + " defeats (" + user1card.getElementDamage(user2card.getElementType()) + ") " +
                            user2.getUsername() + "'s " + user2card.getName() + " (" + user2card.getElementDamage(user1card.getElementType()) + ")");
                    getRewardUser1(user2card);
                }
                else{
                    System.out.println("The Spell/s was/were resisted");
                }
            }
        }
    }

    public void start(){
        //Battles are limited to 100 rounds
        for(int roundCounter = 1; roundCounter <= 100; ++roundCounter){

            //play one round
            System.out.println("-------Round: "+ roundCounter +"-------");
            executeRound(user1.chooseCard(), user2.chooseCard());
            System.out.println("----------------------");

            //check if one player doesn't have any more cards
            if(user1.gameOver() || user2.gameOver()){
                System.out.println("** Game Over **");
                break;
            }
        }

        if(user1.gameOver()){
            System.out.println("** "+ user2.getUsername() + " wins!!! **");
        }
        else if(user2.gameOver()){
            System.out.println("** "+ user1.getUsername() + " wins!!! **");
        }
        else{
            System.out.println("The battle exceeded 100 Rounds. Therefore its a draw!!!");
        }
    }

}
