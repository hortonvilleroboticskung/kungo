import org.json.*;

import java.io.*;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;

public class Main {
    public static void main(String[] args) throws IOException {

            FileInputStream fstream = new FileInputStream("C:\\Users\\kungl\\Desktop\\jfile.json");
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        JSONObject trial = new JSONObject(br.readLine());
        System.out.println(trial.getString("id"));








    }
}
