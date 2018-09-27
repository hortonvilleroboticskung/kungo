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
                    if(comment.substring(0,1).equals("(") && comment.contains(")")){
                        String[] date = comment.split("/|\\)");
                        String day = date[1];
                        String month = date[0].substring(1);
                        String year = "";

                        if(Integer.parseInt(month) >= 1 || Integer.parseInt(month) <= 12){
                            if(Integer.parseInt(month) < 7) year = "2019";
                            else year = "2018";
                        }

                        if(month.length() == 1) month = "0" + month;
                        if(day.length() == 1) day = "0" + day;

                        currentCard.addComment(year+"-"+month+"-"+day+"T",obj.getJSONObject("memberCreator").getString("fullName"),date[2].trim());
                    }else currentCard.addComment(obj.getString("date"),obj.getJSONObject("memberCreator").getString("fullName"),comment.trim());


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

                    Card c = stringCardEntry.getValue();

                    fOut.write("<!--DOCTYPE HTML--><html><body>".getBytes());

                    fOut.write(("<h1 style=\"display:inline;\">"+c.getName()+"</h1>").getBytes());
                    if(c.getLabels().size() != 0)
                        fOut.write(("<h4 style=\"display:inline;padding-left:75px;\">Labels: "+c.getLabels().toString().substring(1,c.getLabels().toString().length()-1)+"</h4>").getBytes());

                    fOut.write("<br><hr>".getBytes());

                    c.getRecord().sort(null);
                    String currDate = "";
                    for(int i = 0; i < c.getRecord().size(); i++) {
                        String entry = c.getRecord().get(i);
                        String[] splitEntry = entry.split("#~#");
                        if(!splitEntry[0].equals(currDate)){
                            if(i != 0)fOut.write("</ul>".getBytes());
                            fOut.write(("<h3>"+splitEntry[0]+"</h3><ul>").getBytes());
                            currDate = splitEntry[0];
                        }
                        if(splitEntry[1].contains("<>")){
                            fOut.write(("<li>"+splitEntry[1].split("<>")[0]+"<br><img height=\"300\" src=\""+splitEntry[1].split("<>")[1]+"\">").getBytes());
                        }else fOut.write(("<li>"+splitEntry[1]+"</li>").getBytes());
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

