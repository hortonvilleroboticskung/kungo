import org.json.*;
import java.util.*;
import java.io.*;
import java.util.function.Consumer;

public class Main {

    public static void main(String[] args) throws IOException{

        FileInputStream fstream = new FileInputStream("C:\\Users\\kungl\\Desktop\\jfile.json");
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        JSONObject jsonfile = new JSONObject(br.readLine());
        JSONArray actions = jsonfile.getJSONArray("actions");
        HashMap<String, Card> cards= new HashMap<>();

        for(int i = actions.length()-1; i >= 0; i--) {

            JSONObject obj = actions.getJSONObject(i);

            if(obj.getJSONObject("data").has("card")) {
                if (obj.getString("type").equals("createCard")) {
                    cards.put(obj.getJSONObject("data").getJSONObject("card").getString("id"),
                            new Card(obj.getString("date"),
                                    obj.getJSONObject("data").getJSONObject("card").getString("id"),
                                    obj.getJSONObject("data").getJSONObject("card").getString("name"),
                                    obj.getJSONObject("data").getJSONObject("list").getString("name")));
                } else if(obj.getString("type").equals("addMemberToCard")) {

                    Card currentCard = cards.get(obj.getJSONObject("data").getJSONObject("card").getString("id"));//looks for addmember type and returns id then cards.get gets the card with that id
                    currentCard.addMember(obj.getString("date"),obj.getJSONObject("memberCreator").getString("fullName"), obj.getJSONObject("data").getJSONObject("member").getString("name"));


                } else if(obj.getString("type").equals("changeList")) {

                    Card currentCard = cards.get(obj.getJSONObject("data").getJSONObject("card").getString("id"));
                    currentCard.changeList(obj.getString("date"), obj.getJSONObject("data").getJSONObject("list").getString("name"));

                } else if(obj.getString("type").equals("commentCard")) {
                    
                    Card currentCard = cards.get(obj.getJSONObject("data").getJSONObject("card").getString("id"));
                    currentCard.addComment(obj.getString("date"),obj.getJSONObject("memberCreator").getString("fullName"),obj.getJSONObject("data").getString("text"));
            
                } else if(obj.getString("type").equals("removeMemberFromCard")){

                    Card currentCard = cards.get(obj.getJSONObject("data").getJSONObject("card").getString("id"));
                    currentCard.removeMember(obj.getString("date"),obj.getJSONObject("memberCreator").getString("fullName"),obj.getJSONObject("data").getJSONObject("member").getString("name"));

                } else if(obj.getString("type").equals("updateCard") && obj.getJSONObject("data").has("id")) {
                    
                    Card currentCard = cards.get(obj.getJSONObject("data").getJSONObject("card").getJSONObject("id"));
                    currentCard.addDescription(obj.getString("date"), obj.getJSONObject("memberCreator").getString("fullName"),obj.getJSONObject("data").getJSONObject("card").getString("desc"));


                } else if(obj.getString("type").equals("addAttachmentToCard") && obj.getJSONObject("data").getJSONObject("attachment").has("url")){
                    Card currentCard = cards.get(obj.getJSONObject("data").getJSONObject("card").getString("id"));
                    currentCard.addAttachment(obj.getString("date"),obj.getJSONObject("memberCreator").getString("fullName"),obj.getJSONObject("data").getJSONObject("attachment").getString("url"));
                }
            }
        }
        cards.entrySet().iterator().forEachRemaining(new Consumer<Map.Entry<String, Card>>() {
            @Override
            public void accept(Map.Entry<String, Card> stringCardEntry) {
                System.out.println(stringCardEntry.getValue().getName()+"->"+stringCardEntry.getValue().getRecord());
            }
        });
    }
}

