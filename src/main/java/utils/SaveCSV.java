package utils;

import lombok.Getter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Getter
public class SaveCSV {

    private boolean status;

    public SaveCSV(List<String> niz, String putanja){
        status = false;
        try {
            File myObj = new File(putanja);
            if (myObj.createNewFile()) {
               // System.out.println("File created: " + myObj.getName());
            } else {
               // System.out.println("File already exists.");
            }
            FileWriter fw = new FileWriter(putanja, false);
            PrintWriter p = new PrintWriter(fw);


            for (String s : niz){
                p.println(s);
            }

            p.close();
            status = true;
          //  System.out.println("asdasdasdasdas");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
