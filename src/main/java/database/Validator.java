package database;

import gui.MainFrame;
import lombok.Getter;
import resource.Pravilo;
import utils.ConfigJson;
import utils.PrettySql;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class Validator {

    private String format;
    private ArrayList<Pravilo> pravila;
    boolean pretpostavka;

    private int brGresaka;

    public Validator(String param) {
        pravila = ConfigJson.pravila();
        format = param;
        brGresaka = 1;
    }

    private Pravilo pronadjiProblem(String naziv) {
        Pravilo resenje = new Pravilo("", "", "");
        for (Pravilo o : pravila) {
            if (o.getNaziv().equals(naziv)) {
                resenje = o;
                break;
            }
        }
        return resenje;
    }

    private void errAppend(String vred) {
        Pravilo p = pronadjiProblem(vred);
        pretpostavka = false;
        MainFrame.getInstance().getEdit().getStPanel().showStackTrace("Error " + brGresaka + ": " + p.getOpis() + ". " + p.getPredlog());
        brGresaka++;
    }



    public boolean goValidate() {
        pretpostavka = true;
        if (format.isEmpty()) {
            pretpostavka = false;
            errAppend("prazno");
        }
        // System.out.println(PrettySql.srediZareze(format) + " RECEE");



       // String[] podaci = PrettySql.srediZareze(format).split(" ");



        List<String> list = new ArrayList<String>();
        Matcher m = Pattern.compile("([^\']\\S*|\'.+?\')\\s*").matcher(PrettySql.srediZareze(format));
        while (m.find())
            list.add(m.group(1));
        for (String s : list){
            System.out.println(s);
        }
        String[] podaci = list.toArray(new String[0]);


        // Selecgt asd, bsa, ss, dndnd where
        String query = "";
       /*
        if (podaci.length < 2) {
            pretpostavka = false;
            errAppend(pronadjiProblem("nepravilno zapoceto"));
        }
        */

        ArrayList<String> reci = PrettySql.ucitaj();
        //  System.out.println("'"+podaci[0]+"'");
        boolean radiWhere = true;
        switch (podaci[0]) {
            case "exec": {
                radiWhere = false;
                osnovniExec(podaci, reci);
            }
            break;
            case "select": {
                osnovniSelect(podaci, reci);
            }
            break;
            case "delete": {
                osnovniDelete(podaci, reci);
            }
            break;
            case "update": {
                osnovniUpdate(podaci, reci);
            }
            break;
            case "insert": {
                radiWhere = false;
                osnovniInsert(podaci, reci);
            }
            break;
            default: {
                errAppend("nepravilno zapoceto");
            }
            break;
        }

        if(radiWhere) {
           // System.out.println("sad radim where ako ima");
        }

        //TODO AS!!!!!!!!!!!!!!
        daLiJeAs(podaci, reci);

        zavrsavaSaTackomZarez(podaci);

        return pretpostavka;
    }

    private void daLiJeAs(String[] podaci, ArrayList<String> reci) {
       // int poz = 0;

        for (int i = 0; i < podaci.length; i++) {
           if ( ukloniSemNavodnika(podaci[i]).equals("as")){
              //  boolean odradio = false;
               if (ukloniSemNavodnika(podaci[i+1]).equals("")) {
                   errAppend("nista posle as");

               }
               boolean nesto = false;
               for(String s : reci) {
                   if (ukloniSemNavodnika(podaci[i+1]).equals(s.trim())) {
                       nesto = true;
                       break;
                   }
               }

               if (nesto) {
                   errAppend("posle as ne kljucna");
               }


               if (ukloniSemNavodnika(podaci[i+1]).startsWith("'")) {
                //   System.out.println(podaci[i+1]+ "asdasdasdasd");
                   if (podaci[i+1].contains("values") || podaci[i+1].contains("from")){
                       errAppend("fale navodnici zat as");
                   }
                   if (!ukloniSemNavodnika(podaci[i+1]).endsWith("'")){
                       errAppend("fale navodnici zat as");
                     //  odradio= true;
                       //   System.out.println("ZATTTT"+ podaci[i]);
                   }
               }
               boolean pret = true;
               if (podaci[i+1].contains(")")) {
                   pret = false;
               }
               if (podaci[i].contains(")")) {
                   pret = false;
               }
               for(String s : reci) {
                   if (ukloniSemNavodnika(podaci[i+1]).equals(s.trim())) {
                       pret = false;
                       break;
                   }
               }

               if (pret ) {
                   //errAppend("fale navodnici ot as");
               }
              // System.out.println(podaci[i + 1]+ "kilsnjiowenfiowefiojweiofjwef" + odradio);

             //  poz = i;
           }
        }



    }


    private void osnovniExec(String[] podaci, ArrayList<String> reci) {
        if (podaci.length <= 1) {
            errAppend("nije odabrana tabela");
            return;
        }
        for (String str : reci) {

            if (podaci[1].equals(str.trim()) || podaci[1].equals(";")) {
                //  pret = true;
                errAppend("nije odabrana atablea exec");
                // System.out.println(podaci[poz + 2]);
                break;
            }
        }
    }

    private void osnovniInsert(String[] podaci, ArrayList<String> reci) {
        if (!podaci[1].equals("into")) {
            errAppend("posle insert into");
        }
        if (podaci.length <= 2) {
            errAppend("nije odabrana tabela");
            return;
        }
        for (String str : reci) {

            if (podaci[2].equals(str.trim()) || podaci[2].equals(";") || podaci[2].equals("(")) { // FIXME odati ; svuda gde se proveravcva tabela
                //  pret = true;
                errAppend("nije odabrana atablea insert");
                // System.out.println(podaci[poz + 2]);
                break;
            }
        }
        if (podaci.length <= 3) {
            errAppend("nepotpun insert fale zagrade");
            return;
        }
        if (!podaci[3].startsWith("(")) {
            errAppend("otvorena zagrada");
        }

            //select kolona1 as mika, kolona2 from nesto
            // select kolona1 as mika zika, kolona2 from nesto

            //  select ' asd'  ' bsa ' 'nnn'
            // select 'asd' 'bsa' 'nnn'
        int poz = 3;
        int brC = 0;
        boolean pret = true;
        boolean zagradaP = false;
        for (int i = 3; i < podaci.length; i++) {
            if (podaci[i].equals("as")) { // FIXME da li je ok da preskacemo sve "'" navodnike

                if (podaci[i+1].contains(")")) {
                    zagradaP = true;
                    //errAppend("fali zat zag insert pre");//treba da izbaci eror stoje random karakteri izmedju navodnika
                }
                i++;
                continue;
            }
            if (podaci[i].contains(")")) {
                zagradaP = true;
                //errAppend("fali zat zag insert pre");//treba da izbaci eror stoje random karakteri izmedju navodnika
                continue;
            }
            if (podaci[i].equals("values")) {
                pret = false;
                //   System.out.println(podaci[i-1]);
               // if (!podaci[i - 1].endsWith(")")) {
               //     errAppend("fali zat zag insert pre");//treba da izbaci eror stoje random karakteri izmedju navodnika
               // }
                poz = i;
                break;
            }
            if (ukloniSemNavodnika(podaci[i]).equals("")) {
                poz = i;
                continue;
            }
          //  System.out.println(podaci[i]+"asdasdas");
            if (!podaci[i].endsWith(",") &&  (!podaci[i+1].equals("as") || !podaci[i+1].equals("values"))) {
                //System.out.println(podaci[i]+"gedhuihuefiwuihefw");
             //   errAppend("fali zarez"); // FIXME MORA NESTO BOLJE
            }
            brC++;
        }
         if (!zagradaP) {
            errAppend("fali zat zag insert pre");
        }
        if (pret) {
            errAppend("fali value");//treba da izbaci gresku da nisu zatvoreni navodnici
        }
       // System.out.println(podaci[poz]+ "SJAOJSIDOAJSIODJAOISDJ");
       // System.out.println(brC);

        if (!podaci[poz + 1].startsWith("(")) {
            errAppend("otvorena zagrada druga");
        }
        int brV =0;
        boolean zagradaD = false;
        for (int i = poz+ 1; i < podaci.length; i++) {

            if (ukloniSemNavodnika(podaci[i]).startsWith("'")) {
                if (!ukloniSemNavodnika(podaci[i]).endsWith("'")){
                    errAppend("fale navodnici zat");
                 //   System.out.println("ZATTTT"+ podaci[i]);
                }
            }
            if (ukloniSemNavodnika(podaci[i]).endsWith("'")) {
                if (!ukloniSemNavodnika(podaci[i]).startsWith("'")){
                    errAppend("fale navodnici ot");
                   // System.out.println("OTVBARAJUCI"+ podaci[i]);
                }
            }
            if (podaci[i].contains(")")) {
                zagradaD = true;
                //errAppend("zatvorena zagrada druga");//treba da izbaci eror stoje random karakteri izmedju navodnika
            }
            if (podaci[i].endsWith(";")) {
                pret = false;
                //   System.out.println(podaci[i-1]);

                poz = i;
                break;
            }
            if (ukloniSemNavodnika(podaci[i]).equals("")) {
                poz = i;
                continue;
            }
            if (!podaci[i].endsWith(",") && (!podaci[i+1].equals("as") || !podaci[i+1].endsWith(";"))) {
                //System.out.println(podaci[i]+"gedhuihuefiwuihefw");
              //  errAppend("fali zarez drugi"); // FIXME MORA NESTO BOLJE
            }
            brV++;
          //  System.out.println(podaci[i]);
        }
        if (!zagradaD) {
            errAppend("zatvorena zagrada druga");
        }
        //System.out.println(brC + " " + brV);
        if (brC < brV) {
           // errAppend("ima manje colona");
        }
        if (brC > brV) {
           // errAppend("ima vise colona");
        }



    }
    //update tabela set kolona = promenjena vrednost where uslov
    private void osnovniUpdate(String[] podaci, ArrayList<String> reci) {
        if (podaci.length <= 2) {
            errAppend("nije odabrana tabela");
            return;
        }

        for (String str : reci) {

            if (podaci[1].equals(str.trim()) || podaci[1].equals(";")) {
                //  pret = true;
                errAppend("nije odabrana tabela");
                // System.out.println(podaci[poz + 2]);
                break;
            }
        }


        if(!podaci[2].equals("set"))
            errAppend("update fali set");


        int poz = 3;
        boolean pret = false;
        boolean sad = false;
        for (int i = 3; i < podaci.length; i++) {

            if(podaci[i].equals("=") || podaci[i].endsWith("=")) {
              //  System.out.println("Usao1" + podaci[i]+i);
                pret = true;
            }

            if (podaci[i].startsWith("=") && !podaci[i].equals("=")) {
             //   System.out.println("Usao2" + podaci[i]+i);
                pret = true;
                sad = true;
            }
            if (pret && sad) {
                //System.out.println("Usao" + podaci[i]+i);
                if (ukloniSemNavodnika(podaci[i]).startsWith("'")) {
                    if (!ukloniSemNavodnika(podaci[i]).endsWith("'")){
                        errAppend("fale navodnici zat u");
                        //   System.out.println("ZATTTT"+ podaci[i]);
                    }
                }
                if (ukloniSemNavodnika(podaci[i]).endsWith("'")) {
                    if (!ukloniSemNavodnika(podaci[i]).startsWith("'")){
                        errAppend("fale navodnici ot u");
                        // System.out.println("OTVBARAJUCI"+ podaci[i]);
                    }
                }
                pret = false;
                sad = false;
            }

            if (pret) {
                sad = true;
            }

            if (podaci[i].equals("where")) {
                //pret = false;
                //   System.out.println(podaci[i-1]);

                poz = i;
                break;
            }
            if (podaci[i].endsWith(";")) {
                //pret = false;
                //   System.out.println(podaci[i-1]);

                poz = i;
                break;
            }
        }


    }

    private void osnovniDelete(String[] podaci, ArrayList<String> reci) {// DELETE FROM asd WHERE asd = absa;
        if (!podaci[1].equals("from")) {
            errAppend("delete pa tag");
        }
        if (podaci.length <= 2) {
            errAppend("nije odabrana tabela");
            return;
        }

        for (String str : reci) {

            if (podaci[2].equals(str.trim()) || podaci[2].equals(";")) {
                //  pret = true;
                errAppend("nije odabrana tabela");
                // System.out.println(podaci[poz + 2]);
                break;
            }
        }


    }


    private void osnovniSelect(String[] podaci, ArrayList<String> reci) {
        boolean pretNemaFrom = false;
        int poz = 1;
        for (int i = 1; i < podaci.length; i++) {
            if (podaci[i].equals("'")) { // FIXME da li je ok da preskacemo sve "'" navodnike
                continue;
            }
            for (String str : reci) {
                if (str.trim().equals("as"))
                    continue;
                if (i > 1 && str.trim().equals("from") && podaci[i].equals(str.trim())) {
                    pretNemaFrom = true;
                    //  System.out.println(podaci[i] + " 1 " + str + i);
                    break;
                }
                if (podaci[i].equals(str.trim()) || podaci[i].equals(";")) {
                    errAppend("select pa tag"); // FIXME situacija "SELECT ON" ili "SELECT WHERE" bilo sta gde ide select pa kljucna rec

                    break;
                }
            }
            if (pretNemaFrom) {
                break;
            }

            poz = i;
        }

        if (!pretNemaFrom) {
            errAppend("fali from");
        }
        //boolean pret = false;
        if (poz + 2 >= podaci.length) {
            errAppend("nije odabrana tabela");
        } else
            for (String str : reci) {

                if (podaci[poz + 2].equals(str.trim()) || podaci[poz + 2].equals(";")) {
                    //  pret = true;
                    errAppend("nije odabrana tabela");
                    // System.out.println(podaci[poz + 2]);
                    break;
                }
            }

    }

    private void zavrsavaSaTackomZarez(String[] podaci) {
        if (!podaci[podaci.length - 1].endsWith(";")) {
            errAppend("ne zavrsava sa tz");
        }
    }

    private String ukloniSemNavodnika(String staro) {
        return staro.trim().replaceAll(";", "").replaceAll("=", "").replaceAll(",", "").replaceAll("\\(", "").replaceAll("\\)", "");
    }

}
