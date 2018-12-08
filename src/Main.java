import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.jpeg.JpegDirectory;
import org.json.*;
import java.net.URL;
import java.util.*;
import java.io.*;
import java.util.function.Consumer;


public class Main {

    public static void attachment(JSONObject obj, Card currentCard){
        String takenDate ="";
        int month = 0;
        URL url = null;
        try {

            if(obj.getString("type").equals("addAttachmentToCard")) {
                url = new URL(obj.getJSONObject("data").getJSONObject("attachment").getString("url"));

            }
//            } else if(obj.getString("type").equals("commentCard")){
//                url = new URL(obj.getJSONObject("data").getString("text"));
//            }

                Metadata metadata = ImageMetadataReader.readMetadata(url.openStream());
                if (metadata.containsDirectoryOfType(ExifIFD0Directory.class)) {
                  //  System.out.println(url);
                    String met = metadata.getDirectoriesOfType(ExifIFD0Directory.class).iterator().next().getDate(0x132).toString();
                    String[] times = met.split(" ");
                    String year = times[5];
                    String day = times[2];
                    switch (times[1]) {
                        case "Jan":
                            month = 1;
                            break;
                        case "Feb":
                            month = 2;
                            break;
                        case "Mar":
                            month = 3;
                            break;
                        case "Apr":
                            month = 4;
                            break;
                        case "May":
                            month = 5;
                            break;
                        case "Jun":
                            month = 6;
                            break;
                        case "Jul":
                            month = 7;
                            break;
                        case "Aug":
                            month = 8;
                            break;
                        case "Sep":
                            month = 9;
                            break;
                        case "Oct":
                            month = 10;
                            break;
                        case "Nov":
                            month = 11;
                            break;
                        case "Dec":
                            month = 12;
                            break;
                    }
                    takenDate = year + "-" + month + "-" + day + "T";
                }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(takenDate.equals("")) {
            System.out.println(url + " " + obj.getString("date"));
            currentCard.addAttachment(obj.getString("date"), obj.getJSONObject("memberCreator").getString("fullName"), obj.getJSONObject("data").getJSONObject("attachment").getString("url"));
        }
        else {
            System.out.println(url );
            currentCard.addAttachment(takenDate,obj.getJSONObject("memberCreator").getString("fullName"),obj.getJSONObject("data").getJSONObject("attachment").getString("url"));
        }

    }


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
                        if((comment.substring(comment.length()-3).equals("jpg")) || (comment.substring(comment.length()-3).equals("png")) || (comment.substring(comment.length()-3).equals("PNG")) ){
                            attachment(obj,currentCard);
                        }
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
                        attachment(obj,currentCard);
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
                            int height,width;
                            String output = "";
                            try{
                                URL url = new URL(splitEntry[1].split("<>")[1]);
                                Metadata meta = ImageMetadataReader.readMetadata(url.openStream());
                                if(meta.containsDirectoryOfType(JpegDirectory.class)) {
                                    height = meta.getDirectoriesOfType(JpegDirectory.class).iterator().next().getImageHeight();
                                    width = meta.getDirectoriesOfType(JpegDirectory.class).iterator().next().getImageWidth();

                                    if (height >= width) {
                                        output = "\"height:300px;\"";

                                    } else if (width > height) {
                                        output = "\"width:300px;\"";
                                    } else {
                                        output = "\"width:auto;height:auto;\"";
                                    }
                                } else {
                                    output = "\"width:auto;height:auto;\"";
                                }
                            }catch(Exception e){
                                e.printStackTrace();
                                output="\"width:300px;\"";
                            }

                            fOut.write(("<li>"+splitEntry[1].split("<>")[0]
                                    +"<br><img style="+output+" src=\""+splitEntry[1].split("<>")[1]+"\">").getBytes());
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