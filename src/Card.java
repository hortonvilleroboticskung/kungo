import java.io.*;
import org.json.*;
import java.util.*;

public class Card {

    String id;
    String name;
    String currentList;
    private ArrayList<String> comments;
    private ArrayList<String> members;
    private ArrayList<String> labels;
    public final String kungId = "5b71c696311f123b5bf6db7d";

    public Card(String id, String name, String currentList) {
        this.id = id;
        this.name = name;
        this.currentList = currentList;
        comments = new ArrayList<>();
        members = new ArrayList<>();
        labels = new ArrayList<>();
    }

    public void addMember(Card card, String memberId) {

        card.members.add(memberId);

    }

    public void showMembers(Card card) {

    }
//    public static void main(String[] args){
//        Card a = new Card("a","a","a");
//    }

}