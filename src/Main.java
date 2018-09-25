import org.json.*;

import java.awt.*;
import java.util.*;
import java.io.*;
import java.util.function.Consumer;

public class Main {

    public static void main(String[] args) throws IOException{

        FileInputStream fstream = new FileInputStream(".\\jfile.json");
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        JSONObject jsonfile = new JSONObject(br.readLine());
        JSONArray actions = jsonfile.getJSONArray("actions");
        JSONArray cardArr = jsonfile.getJSONArray("cards");
        HashMap<String, Card> cards= new HashMap<>();
        HashMap<String,String> labels = new HashMap<>();

        for(int i = actions.length()-1; i >= 0; i--) {

            JSONObject obj = actions.getJSONObject(i);

            if(obj.getJSONObject("data").has("card")) {
                Card currentCard = cards.get(obj.getJSONObject("data").getJSONObject("card").getString("id"));
                if (obj.getString("type").equals("createCard")) {
                    cards.put(obj.getJSONObject("data").getJSONObject("card").getString("id"),
                            new Card(obj.getString("date"),
                                    obj.getJSONObject("data").getJSONObject("card").getString("id"),
                                    obj.getJSONObject("data").getJSONObject("card").getString("name"),
                                    obj.getJSONObject("data").getJSONObject("list").getString("name")));
                } else if(obj.getString("type").equals("addMemberToCard")) {
                    currentCard.addMember(obj.getString("date"),obj.getJSONObject("memberCreator").getString("fullName"), obj.getJSONObject("data").getJSONObject("member").getString("name"));

                } else if(obj.getString("type").equals("changeList")) {
                    currentCard.changeList(obj.getString("date"), obj.getJSONObject("data").getJSONObject("list").getString("name"));

                } else if(obj.getString("type").equals("commentCard")) {
                        String comment = obj.getJSONObject("data").getString("text");
                        currentCard.addComment(obj.getString("date"),obj.getJSONObject("memberCreator").getString("fullName"),comment);
                        String[] date = comment.split("/|\\)");
                        if(date.length==3){
                            int day = Integer.parseInt(date[1]);
                            int month = Integer.parseInt(date[0].substring(1));
                            System.out.println(month+"-"+day);
                            System.out.println(date[0]);
                        }


                } else if(obj.getString("type").equals("removeMemberFromCard")){
                    currentCard.removeMember(obj.getString("date"),obj.getJSONObject("memberCreator").getString("fullName"),obj.getJSONObject("data").getJSONObject("member").getString("name"));

                } else if(obj.getString("type").equals("updateCard") && obj.getJSONObject("data").has("card") && obj.getJSONObject("data").getJSONObject("card").has("desc")) {
                    currentCard.addDescription(obj.getString("date"), obj.getJSONObject("memberCreator").getString("fullName"),obj.getJSONObject("data").getJSONObject("card").getString("desc"));

                } else if(obj.getString("type").equals("addAttachmentToCard") && obj.getJSONObject("data").getJSONObject("attachment").has("url")){
                    currentCard.addAttachment(obj.getString("date"),obj.getJSONObject("memberCreator").getString("fullName"),obj.getJSONObject("data").getJSONObject("attachment").getString("url"));

                } else if(obj.getString("type").equals("updateCard") && obj.getJSONObject("data").has("listAfter")){
                    currentCard.changeList(obj.getString("date"),obj.getJSONObject("data").getJSONObject("listAfter").getString("name"));
                }
            }
        }

        for(int i = cardArr.length()-1; i >=0;i--){
            JSONObject obj = cardArr.getJSONObject(i);
            Card currentCard = cards.get(obj.getString("id"));
            for(int a = 0; a <= obj.getJSONArray("labels").length()-1; a++) {
                labels.put(obj.getJSONArray("labels").getJSONObject(a).getString("id"), obj.getJSONArray("labels").getJSONObject(a).getString("name"));
            }
            if(obj.getJSONArray("idLabels").length()!=0){
                for(int a = 0; a <= obj.getJSONArray("idLabels").length()-1;a++) currentCard.addLabel(labels.get(obj.getJSONArray("idLabels").getString(a)));

            }
        }



        cards.entrySet().iterator().forEachRemaining(new Consumer<Map.Entry<String, Card>>() {
            @Override
            public void accept(Map.Entry<String, Card> stringCardEntry) {

                try {
                    File folder = new File(System.getProperty("user.home")+"/Desktop/TrelloCardOutput");
                    if (!folder.exists()) folder.mkdirs();
                    File out = new File(folder,stringCardEntry.getValue().getName()+".html");

                    FileOutputStream fOut = new FileOutputStream(out,true);
                    FileOutputStream fClear = new FileOutputStream(out);

                    fClear.write("".getBytes());
                    fClear.flush();
                    fClear.close();

                    fOut.write("<!--DOCTYPE HTML--><html><body>".getBytes());

                    fOut.write(("<h1>"+stringCardEntry.getValue().getName()+"</h1>").getBytes());

                    Card c = stringCardEntry.getValue();
                    c.getRecord().sort(null);
                    String currDate = "";
                    for(String entry : c.getRecord()) {
                        String[] splitEntry = entry.split(":");
                        if(!splitEntry[0].equals(currDate)){
                            fOut.write(("<h3>"+splitEntry[0]+"</h3>").getBytes());
                            currDate = splitEntry[0];
                        }

                    }

                    fOut.write("</body></html>".getBytes());
                    fOut.flush();
                    fOut.close();
                }catch(Exception e){e.printStackTrace();}
                System.out.println(stringCardEntry.getValue().getName()+"->"+stringCardEntry.getValue().getRecord());
                System.out.println(stringCardEntry.getValue().getLabels());
            }
        });
    }
}

