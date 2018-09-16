import org.json.*;
import java.util.*;
import java.io.*;
import java.lang.reflect.Array;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;

public class Main {
    public static void main(String[] args) throws IOException {

        FileInputStream fstream = new FileInputStream("C:\\Users\\kungl\\Desktop\\jfile.json");
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        JSONObject trial = new JSONObject(br.readLine());
        JSONArray actions = trial.getJSONArray("actions");
        String boardName = "";
        String boardId = "";
        String listName = "";
        String listId = "";
        ArrayList<String> cardName = new ArrayList<>();

        listName = actions.getJSONObject(1).getJSONObject("data").getJSONObject("list").getString("name");
        listId = actions.getJSONObject(1).getJSONObject("data").getJSONObject("list").getString("id");
        boardName = actions.getJSONObject(1).getJSONObject("data").getJSONObject("board").getString("name");

          System.out.println(listId);
          System.out.println(boardName);
          System.out.println(listName);
}








    }

