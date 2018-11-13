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
                JSONObject objData = obj.getJSONObject("data");
                String objType = obj.getString("type");
                String objDate = obj.getString("date");
                String memberName = obj.getJSONObject("memberCreator").getString("fullName");

                if(objData.has("card")) {

                    Card currentCard = cards.get(objData.getJSONObject("card").getString("id"));

                    if (objType.equals("createCard") || objType.equals("copyCard")) {
                        cards.put(objData.getJSONObject("card").getString("id"),
                                new Card(objDate,
                                        objData.getJSONObject("card").getString("id"),
                                        objData.getJSONObject("card").getString("name"),
                                        objData.getJSONObject("list").getString("name")));
                    }

                    else if(objType.equals("addMemberToCard")) {
                        currentCard.addMember(objDate,memberName, objData.getJSONObject("member").getString("name"));
                    }

                    else if(objType.equals("changeList")) {
                        currentCard.changeList(objDate, objData.getJSONObject("list").getString("name"));
                    }

                    else if(objType.equals("commentCard")) {
                        String comment = objData.getString("text");
                        if(comment.substring(0,1).equals("(") && comment.contains(")")) {
                            String[] date = comment.split("/|\\)");

                            String day = date[1];
                            String month = date[0].substring(1);
                            String year = "";
                            if (Integer.parseInt(month) >= 1 || Integer.parseInt(month) <= 12) {
                                if (Integer.parseInt(month) < 7) year = "2019";
                                else year = "2018";
                            }

                            if (month.length() == 1) month = "0" + month;
                            if (day.length() == 1) day = "0" + day;

                            currentCard.addComment(year + "-" + month + "-" + day + "T", memberName, date[2].trim());
                        }else if(comment.split("\\[.+\\]\\(.+\\)").length==2){
                            currentCard.addComment(objDate,memberName,comment.split("\\[.+\\]\\(.+\\)")[1].trim());
                        } else {
//                            String[] a = comment.split("\\[.+\\]\\(.+\\)");
//                            if(a.length==2)System.out.println(a[1]);
                            currentCard.addComment(objDate, memberName, comment.trim());
                        }
                    }

                    else if(objType.equals("removeMemberFromCard")){
                        currentCard.removeMember(objDate,memberName,objData.getJSONObject("member").getString("name"));
                    }

                    else if(objType.equals("updateCard") && objData.has("card") && objData.getJSONObject("card").has("desc")) {
                        currentCard.addDescription(objDate, memberName,objData.getJSONObject("card").getString("desc"));
                    }

                    else if(objType.equals("addAttachmentToCard") && objData.getJSONObject("attachment").has("url")){
                        currentCard.addAttachment(objDate,memberName,objData.getJSONObject("attachment").getString("url"));
                    }

                    else if(objType.equals("updateCard") && objData.has("listAfter")){
                         currentCard.changeList(objDate,objData.getJSONObject("listAfter").getString("name"));
                    }
                }
            }

        for(int i = cardArr.length()-1; i >=0;i--){

            JSONObject obj = cardArr.getJSONObject(i);
            Card currentCard = cards.get(obj.getString("id"));

            for(int a = 0; a <= obj.getJSONArray("labels").length()-1; a++) {
                labels.put(obj.getJSONArray("labels").getJSONObject(a).getString("id"),
                        obj.getJSONArray("labels").getJSONObject(a).getString("name"));
            }

            if(obj.getJSONArray("idLabels").length()!=0){
                for(int a = 0; a <= obj.getJSONArray("idLabels").length()-1;a++)
                    currentCard.addLabel(labels.get(obj.getJSONArray("idLabels").getString(a)));
            }
        }



        cards.entrySet().iterator().forEachRemaining(new Consumer<Map.Entry<String, Card>>() {
            @Override
            public void accept(Map.Entry<String, Card> stringCardEntry) {

                try {
                    File folder = new File(System.getProperty("user.home") + "/Desktop/TrelloCardOutput");
                    if (!folder.exists()) folder.mkdirs();
                    File out = new File(folder, stringCardEntry.getValue().getName() + ".html");

                    FileOutputStream fOut = new FileOutputStream(out, true);
                    FileOutputStream fClear = new FileOutputStream(out);

                    fClear.write("".getBytes());
                    fClear.flush();
                    fClear.close();

                    Card c = stringCardEntry.getValue();

                    fOut.write("<!--DOCTYPE HTML--><html><head><style>li{margin-bottom:15px;}</style></head><body style=font-family:arial;color:red>".getBytes());

                    fOut.write(("<h1 style=\"display:inline;\">" + c.getName() + "</h1>").getBytes());
                    if (c.getLabels().size() != 0){
                        fOut.write(("<h4 style=\"display:inline;padding-left:75px;\">Labels: "
                                + c.getLabels().toString().substring(1, c.getLabels().toString().length() - 1) + "</h4>").getBytes());
                    }

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
                            fOut.write(("<li>"+splitEntry[1].split("<>")[0]
                                    +"<br><img style=\"height:auto;width:auto;\" src=\""+splitEntry[1].split("<>")[1]+"\">").getBytes());
                        }else {
                            fOut.write(("<li>" + splitEntry[1] + "</li>").getBytes());
                        }
                    }


                    fOut.write("</body></html>".getBytes());
                    fOut.flush();
                    fOut.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}

