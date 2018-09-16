import org.json.*;
import java.util.*;
import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        FileInputStream fstream = new FileInputStream("C:\\Users\\kungl\\Desktop\\jfile.json");
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        ArrayList<String> twoadd = new ArrayList<>();
        String[] addtoo;
        String[] tooadd = null;
        addtoo = br.readLine().split(",");
        //for (int i = 0; i < addtoo.length; i++) {
            //System.out.println(addtoo[i]);
        //}
        for(int i = 0; i < addtoo.length; i++) {
            tooadd = addtoo[i].split(":");
            for(int a = 0; a < tooadd.length; a++)
            twoadd.add(tooadd[a]);
        }
        for(int i = 0; i < twoadd.size(); i++) {
            System.out.println(twoadd.get(i));
        }


    }
}
