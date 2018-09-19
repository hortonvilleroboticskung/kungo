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
        HashMap<String, Integer> idToIndex= new HashMap<>();

        for(int i = actions.length()-1; i >= 0; i--) {
            JSONObject obj = actions.getJSONObject(i);
            //Example (Adding card)
            if(obj.has("card")) {
                if (obj.getString("type").equals("createCard")) {
                    idToIndex.put(obj.getJSONObject("card").getString("id"), cards.size()-1);
                    cards.add(new Card(obj.getString("date"), obj.getJSONObject("card").getString("id"), obj.getJSONObject("card").getString("name"), obj.getJSONObject("list").getString("name")));
                } else if(obj.getString("type").equals("INSERT TYPE HERE")) {
                    Card currentCard = cards.get(idToIndex.get(obj.getJSONObject("card").getString("id")));
                    //ADD CHANGES TO CURRENTCARD
                }
            }
        }
    }
}

