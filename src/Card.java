import java.io.*;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import org.json.*;

import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class Card {

    private String id;
    private String name;
    private String currentList;
    private String description;
    private HashMap<String, String> comments;
    private ArrayList<String> members;
    private ArrayList<String> labels;
    private ArrayList<String> masterRecord;
    private ArrayList<String> attachments;

    public Card(String timeStamp, String id, String name, String currentList) {
        masterRecord = new ArrayList<>();
        addToRecord(timeStamp,"Task created under \""+currentList+"\" list.");
        this.id = id;
        this.name = name;
        this.currentList = currentList;
        comments = new HashMap<>();
        members = new ArrayList<>();
        labels = new ArrayList<>();
        attachments = new ArrayList<>();

    }

    public void addMember(String timeStamp,String sourceName, String memberName) {
        addToRecord(timeStamp,""+formatName(memberName)+" added to task by "+formatName(sourceName)+".");
        members.add(memberName);
    }

    public void removeMember(String timeStamp, String sourceName, String memberName){
        addToRecord(timeStamp, formatName(memberName)+" removed from task by "+formatName(sourceName)+".");
        members.remove(memberName);
    }

    public void changeList(String timeStamp, String newList){
        addToRecord(timeStamp,"Moved from \""+currentList+"\" to \""+newList+"\".");
        currentList = newList;
    }

    public void addComment(String timeStamp, String memberName, String content){
        addToRecord(timeStamp,formatName(memberName)+" commented \""+content.replace("\n","")+"\".");
        comments.put(memberName,content);
    }

    public void addDescription(String timeStamp, String memberName, String content) {
        addToRecord(timeStamp, formatName(memberName)+" added description the description \""+content+"\".");
        description = content;
    }

    public void addAttachment(String timeStamp, String sourceMember, String url){
        try {
            URL u = new URL(url);
            URLConnection c = u.openConnection();
            Metadata metadata = ImageMetadataReader.readMetadata(c.getInputStream());
            System.out.println("Metadata: " + metadata.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        addToRecord(timeStamp, formatName(sourceMember)+ " added attachment:<>"+url);
        attachments.add(url);
    }

    public void addLabel(String content){
        labels.add(content);
    }

    public ArrayList<String> getLabels(){return labels;}

    public void changeLabels(String timeStamp, String[] labelList) {
        String listOfLabels = labelList[0];
        for (int i = 1; i < labelList.length; i++) listOfLabels += ", " + labelList[i];
        addToRecord(timeStamp, "Tagged as " + listOfLabels + ".");
        labels.clear();
        for (String s : labelList) labels.add(s);
    }

    public String getId(){return id;}
    public String getName(){return name;}
    public ArrayList<String> getRecord(){return masterRecord;}

    private void addToRecord(String timeStamp, String contents){
        masterRecord.add(formatDate(timeStamp)+"#~#"+contents);
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