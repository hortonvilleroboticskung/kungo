import java.io.*;
import org.json.*;
import java.util.*;

public class Card {

    private String id;
    private String name;
    private String currentList;
    private HashMap<String, String> comments;
    private ArrayList<String> members;
    private ArrayList<String> labels;
    private ArrayList<String> masterRecord;

    public Card(String timeStamp, String id, String name, String currentList) {
        masterRecord = new ArrayList<>();
        addToRecord(timeStamp,"Task created under \""+currentList+"\" list.");
        this.id = id;
        this.name = name;
        this.currentList = currentList;
        comments = new HashMap<>();
        members = new ArrayList<>();
        labels = new ArrayList<>();

    }

    public void addMember(String timeStamp, String memberName) {
        addToRecord(timeStamp,""+formatName(memberName)+" added to task.");
        members.add(memberName);
    }

    public void changeList(String timeStamp, String newList){
        addToRecord(timeStamp,"Moved from \""+currentList+"\" to \""+newList+"\".");
        currentList = newList;
    }

    public void addComment(String timeStamp, String memberName, String content){
        addToRecord(timeStamp,formatName(memberName)+" commented \""+content+"\".");
        comments.put(memberName,content);
    }

    public void changeLabels(String timeStamp, String[] labelList){
        String listOfLabels = labelList[0];
        for(int i = 1; i < labelList.length; i++) listOfLabels +=", "+labelList[i];
        addToRecord(timeStamp, "Tagged as "+listOfLabels+".");
        labels.clear();
        for(String s : labelList) labels.add(s);
    }

    public String getId(){return id;}

    private void addToRecord(String timeStamp, String contents){
        masterRecord.add(formatDate(timeStamp)+":"+contents);
    }

    private String formatName(String name){
        return name.substring(0,1).toUpperCase()+name.substring(1);
    }

    private String formatDate(String timeStamp){
        return timeStamp.split("T")[0];
    }

    @Override
    public boolean equals(Object o){
        return o instanceof Card ? ((Card) o).getId().equals(getId()) : o instanceof String && o.equals(getId());
    }

}