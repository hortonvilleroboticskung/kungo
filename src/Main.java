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

        for(int i = actions.length()-1; i >= 0; i--) {
            JSONObject obj = actions.getJSONObject(i);
            //TODO:INSERT CODE HERE
        }
    }

}

