package utils;

import lombok.Getter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Getter
public class ParserCSV {

    private final ArrayList<String> insert;
    private boolean error = false;

    public ParserCSV(String putanja, String tabela, List<String> baza) {
       insert = new ArrayList<>();
       try {
           FileReader file = new FileReader(putanja);
           Scanner s = new Scanner(file);
            boolean jednom = true;
            String colone = "";
           while(s.hasNextLine()){
               if (jednom){
                   colone = s.nextLine();
                 //  System.out.println(colone);
                   String[] razbijeno = colone.split(",");
                   for (String a : razbijeno) {
                       if (!baza.contains(a.trim())) {
                          // System.out.println(a + "asdasd");
                           error = true;
                           break;
                       }
                   }
                   if (error){
                       break;
                   }
                   jednom = false;
                   continue;
               }


               StringBuilder linija = new StringBuilder("INSERT INTO " + tabela + " (" + colone);
             /*  for (int i = 1; i < baza.size(); i++) {
                   linija.append(", ").append(baza.get(i));
               }

              */

               String[] razbijeno = s.nextLine().split(",");
               if (razbijeno.length == 0) {

                  // continue;
               }
               for (String sssss : razbijeno){
                   System.out.println(sssss+" reioioerwjiowerjfiowjfiowejfiwejfoiawejgiowjrg");
               }

               linija.append(") VALUES ('").append(razbijeno[0]).append("'");
               for (int i = 1; i < razbijeno.length; i++) {
                    linija.append(", '").append(razbijeno[i].trim()).append("'");
               }
               linija.append(");");
               insert.add(String.valueOf(linija));
              // System.out.println(linija);
           }
       }catch (FileNotFoundException e){
                e.printStackTrace();
       }

    }

}
