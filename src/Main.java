import org.json.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.Array;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;

public class Main {
//.has
    public static void main(String[] args) throws IOException{

        FileInputStream fstream = new FileInputStream("C:\\Users\\kungl\\Desktop\\jfile.json");
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        JSONObject jsonfile = new JSONObject(br.readLine());
        JSONArray actions = jsonfile.getJSONArray("actions");
        ArrayList<Card> cards = new ArrayList<>();

        for(int i = actions.length()-1; i >= 0; i--){
            if(actions.getJSONObject(i).getString("type").equals("createCard")){
                Card newCard = new Card(
                        actions.getJSONObject(i).getJSONObject("data").getJSONObject("card").getString("id"),
                        actions.getJSONObject(i).getJSONObject("data").getJSONObject("card").getString("name"),
                        actions.getJSONObject(i).getJSONObject("data").getJSONObject("list").getString("name")
                );
                cards.add(newCard);
                //System.out.println(newCard.id + "\n" + newCard.name + "\n" + newCard.currentList);
            }
            if(actions.getJSONObject(i).getString("type").equals("addMemberToCard") && cards.size()!=0){
                for(int ii = 0; ii < cards.size(); ii++){
                    if(cards.get(ii).id.equals(actions.getJSONObject(i).getJSONObject("data").getJSONObject("card").getString("id"))){
                        cards.get(ii).addMember(cards.get(ii),actions.getJSONObject(i).getJSONObject("member").getString("fullName"));//TODO:Can i get it so i can just use the object as a parameter?
                    }
                }
            }
        }

    }

}

