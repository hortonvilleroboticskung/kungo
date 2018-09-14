import org.json.*;

import java.io.*;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;

public class Main {
    public static void main(String[] args)
    {
        File data = new File("C:\\Users\\heuve\\Desktop\\file.txt");
        String dataString = "";
        try {
            if(!data.exists()){
                data.createNewFile();
            }
            InputStream i = new FileInputStream(data);
            byte[] in = new byte[(int)data.length()];
            i.read(in);
            dataString = new String(in);
        } catch(Exception e) {
            e.printStackTrace();
        }

        JSONObject trial = new JSONObject(dataString);
        System.out.println(trial.getJSONObject("name").get("first"));
        System.out.println(trial.getJSONObject("actions").get("id"));
//ljlkjldkasd



    }
}