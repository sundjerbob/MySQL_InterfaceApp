package utils;

import org.json.JSONArray;
import org.json.JSONObject;
import resource.Pravilo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ConfigJson {

    /*
    ArrayList<Pravilo> p = ConfigJson.pravila();
        for (Pravilo o :p ) {
        System.out.println(o.toString());
    }
    */

    public static ArrayList<Pravilo> pravila() {
        ArrayList<Pravilo> resenje = new ArrayList<>();
        try {
            File file = new File("pravila.json");
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            fis.close();
            String str = new String(data, StandardCharsets.UTF_8);
            JSONArray j = new JSONArray(str);
            for (int i = 0; i < j.length(); i++) {
                String naziv = j.getJSONObject(i).getString("pravilo");
                String opis = j.getJSONObject(i).getString("opis");
                String predlog = j.getJSONObject(i).getString("predlog");
                resenje.add(new Pravilo(naziv, opis, predlog));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resenje;
    }
}