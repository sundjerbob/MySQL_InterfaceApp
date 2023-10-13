package database;

import gui.MainFrame;
import lombok.Getter;
import resource.Pravilo;
import utils.ConfigJson;
import utils.PrettySql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class QueryChecker {

    private String query;
    private ArrayList<Pravilo> pravila;
    private int brGresaka;
    private boolean uspesno;

    private ArrayList<String> agregacije;

    public QueryChecker(String query) {
        this.query = query;
        pravila = ConfigJson.pravila();
        brGresaka = 1;
        agregacije = new ArrayList<>();
        agregacije.add("count(");
        agregacije.add("max");
        agregacije.add("min");
        agregacije.add("avg");


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
        uspesno = false;
        MainFrame.getInstance().getEdit().getStPanel().showStackTrace("Query error " + brGresaka + ": " + p.getOpis() + ". " + p.getPredlog());
        brGresaka++;
    }

    private void errAppend(String vred, String ko) {
        Pravilo p = pronadjiProblem(vred);
        uspesno = false;
        MainFrame.getInstance().getEdit().getStPanel().showStackTrace("Query error " + brGresaka + ": " + p.getOpis() + ko + ". " + p.getPredlog());
        brGresaka++;
    }

    public boolean goQueryCheck() {
        uspesno = true;

        if (query.isEmpty()) {
            uspesno = false;
            errAppend("prazno");
        }
        // System.out.println(PrettySql.srediZareze(format) + " RECEE");


       // String[] podaci = PrettySql.srediZareze(query).split(" ");


        List<String> list = new ArrayList<String>();
        Matcher m = Pattern.compile("([^\']\\S*|\'.+?\')\\s*").matcher(PrettySql.srediZareze(query));
        while (m.find())
            list.add(m.group(1));
        for (String s : list){
            System.out.println(s);
        }
        String[] podaci = list.toArray(new String[0]);




        ArrayList<String> reci = PrettySql.ucitaj();

        boolean radiWhere = true;
        switch (podaci[0]) {
            case "exec": {
                radiWhere = false;
                osnovniExecQ(podaci, reci);
            }
            break;
            case "select": {
                osnovniSelectQ(podaci, reci);
            }
            break;
            case "delete": {
                osnovniDeleteQ(podaci, reci);
            }
            break;
            case "update": {
                osnovniUpdateQ(podaci, reci);
            }
            break;
            case "insert": {
                radiWhere = false;
                osnovniInsertQ(podaci, reci);
            }
            break;
            default: {
                errAppend("nepravilno zapoceto");
            }
            break;
        }

        if (radiWhere) {
           // System.out.println("sad radim where ako ima");
        }


        return uspesno;
    }




    private void osnovniDeleteQ(String[] podaci, ArrayList<String> reci) {

        String imeTabele = ukloni(podaci[2]);
        List<String> imenaTabela = MainFrame.getInstance().getAppCore().loadTableNames();
        if (!imenaTabela.contains(imeTabele)) {
            errAppend("ne postoji tabela", imeTabele);
        }
    }

    private void osnovniUpdateQ(String[] podaci, ArrayList<String> reci) {
        String imeTabele = ukloni(podaci[1]);
        List<String> imenaTabela = MainFrame.getInstance().getAppCore().loadTableNames();
        ArrayList<String> colone = new ArrayList<>();
        ArrayList<String> vred = new ArrayList<>();
        List<String> imenaKolona = new ArrayList<>();

        if (!imenaTabela.contains(imeTabele)) {
            errAppend("ne postoji tabela", imeTabele);
        } else {
            imenaKolona = MainFrame.getInstance().getAppCore().loadColNames(imeTabele);
        }
        int poz = 3;
        boolean kome = false;
        for (int i = 3; i < podaci.length; i++) {
            if (podaci[i].equals("'")) { // FIXME da li je ok da preskacemo sve "'" navodnike
                continue;
            }
            if (podaci[i].trim().equals("=")) { // FIXME da li je ok da preskacemo sve "'" navodnike
                continue;
            }
            if (podaci[i].trim().equals(",")) { // FIXME da li je ok da preskacemo sve "'" navodnike
                continue;
            }
            kome = !kome;
            if (podaci[i].equals("where")) {
                //   System.out.println(podaci[i-1]);

                poz = i;
                break;
            }

            if (podaci[i].endsWith(";")) {
                //   System.out.println(podaci[i-1]);
                if (!ukloni(podaci[i]).equals("")) {
                    if (kome) {
                        colone.add(ukloni(podaci[i]));
                    }else{
                        vred.add(ukloni(podaci[i]));
                    }
                }
                poz = i;
                break;
            }

            boolean a = false;
            for (String s : reci) {
                if (ukloni(podaci[i]).trim().equals(s.trim())) {
                    a = true;
                    break;
                }
            }

            if (a) {
                poz = i;
                continue;
            }
            if (ukloni(podaci[i]).equals("")) {
                poz = i;
                continue;
            }
            if (kome) {
                colone.add(ukloni(podaci[i]));
            }else{
                vred.add(ukloni(podaci[i]));
            }

            poz = i;

        }

        //System.out.println(colone);
        //System.out.println(vred);
        for (String col : colone) {
            if (!imenaKolona.contains(col)) {
                // System.out.println();
                errAppend("naveden ne postoji", col);
            }
        }

        if (colone.size() != vred.size()) {
            errAppend("netacan br", colone.size() + " a ima " + vred.size());
        }


    }

    private void osnovniInsertQ(String[] podaci, ArrayList<String> reci) {
        ArrayList<String> colone = new ArrayList<>();
        String imeTabele = ukloni(podaci[2]);

        List<String> imenaTabela = MainFrame.getInstance().getAppCore().loadTableNames();
        List<String> imenaKolona = new ArrayList<>();

        if (!imenaTabela.contains(imeTabele)) {
            errAppend("ne postoji tabela", imeTabele);
        } else {
            imenaKolona = MainFrame.getInstance().getAppCore().loadColNames(imeTabele);
        }

        int poz = 3;
       // boolean preksoci = false;
        for (int i = 3; i < podaci.length; i++) {
          //  if (podaci[i].equals("as")) { // FIXME da li je ok da preskacemo sve "'" navodnike
              //  i++;
         //       continue;
         //   }

            if (podaci[i].equals("values")) {
                //   System.out.println(podaci[i-1]);

                poz = i;
                break;
            }
            boolean a = false;
            for (String s : reci) {
                if (ukloni(podaci[i]).trim().equals(s.trim())) {
                    a = true;
                    break;
                }
            }

            if (a) {
                poz = i;
                continue;
            }
            if (ukloni(podaci[i]).equals("")) {
                poz = i;
                continue;
            }
            colone.add(ukloni(podaci[i]));

            poz = i;

        }

        for (String col : colone) {
            if (!imenaKolona.contains(col)) {
                // System.out.println();
                errAppend("naveden ne postoji", col);
            }
        }

        ArrayList<String> vred = new ArrayList<>();

        for (int i = poz; i < podaci.length; i++) {
            if (podaci[i].equals("'")) { // FIXME da li je ok da preskacemo sve "'" navodnike
                continue;
            }
            if (podaci[i].equals(",")) { // FIXME da li je ok da preskacemo sve "'" navodnike
                continue;
            }
            if (podaci[i].endsWith(")")) {
                if (!ukloni(podaci[i]).equals("")) {
                    vred.add(ukloni(podaci[i]));
                }
                poz = i;
                break;
            }
            if (podaci[i].endsWith(";")) {
                if (!ukloni(podaci[i]).equals("")) {
                    vred.add(ukloni(podaci[i]));
                }
                poz = i;
                break;
            }
            boolean a = false;
            for (String s : reci) {
                if (ukloni(podaci[i]).trim().equals(s.trim())) {
                    a = true;
                    break;
                }
            }

            if (a) {
                poz = i;
                continue;
            }

            vred.add(ukloni(podaci[i]));

            poz = i;

        }

        if (colone.size() != vred.size()) {
            errAppend("netacan br", colone.size() + " a ima " + vred.size());
        }



       // System.out.println(colone);
       // System.out.println(vred);

    }

    private void osnovniExecQ(String[] podaci, ArrayList<String> reci) {



    }

    private void osnovniSelectQ(String[] podaci, ArrayList<String> reci) {
        int poz = 1;
        boolean zvezda = false;

        ArrayList<String> colone = new ArrayList<>();
        if (podaci[1].trim().equals("*")) {
            zvezda = true;
        }
        for (int i = 1; i < podaci.length; i++) {

            if (podaci[i].equals("'")) { // FIXME da li je ok da preskacemo sve "'" navodnike
                continue;
            }
            if (podaci[i].equals(";")) {
                poz = i;
                continue;
            }

            if (podaci[i].trim().equals("from")) {
                break;
            }
            boolean a = false;
            for (String s : reci) {
                if (ukloni(podaci[i]).trim().equals(s.trim())) {
                    if (s.trim().equals("as")) {
                        i++;
                    }
                    a = true;
                    break;
                }
            }

            if (a) {
                poz = i;
                continue;
            }
            if (ukloni(podaci[i]).equals("")) {
                poz = i;
                continue;
            }

            colone.add(ukloniBezZagrada(podaci[i]));
            poz = i;
        }

        String imeTabele = ukloni(podaci[poz + 2]);

        List<String> imenaTabela = MainFrame.getInstance().getAppCore().loadTableNames();
        List<String> imenaKolona = new ArrayList<>();

        if (!imenaTabela.contains(imeTabele)) {
            errAppend("ne postoji tabela", imeTabele);
        } else {
            imenaKolona = MainFrame.getInstance().getAppCore().loadColNames(imeTabele);
        }
        if (!zvezda) {
            boolean imaAgregat = false;
            int koliko = 0;
            for (String col : colone) {
                boolean prekoci = false;
                for (String o : agregacije){
                    if (col.startsWith(o)) {
                        koliko++;
                        prekoci = true;
                        imaAgregat = true;
                        break;
                    }
                }
                if (prekoci){
                    continue;
                }
                if (!imenaKolona.contains(col) ) {
                    // System.out.println();
                    boolean provera = true;
                    for (String op:podaci){
                        if (op.contains("join")){
                            provera = false;
                        }
                    }
                     if (provera)
                    errAppend("naveden ne postoji", col);
                }
            }
            int poz2 = 0;
           for (int i = 0; i < podaci.length; i++) {
                if (podaci[i].contains("join")){
                    String ime = podaci[i+1];
                    List<String> zaJoin = new ArrayList<>();
                    zaJoin = MainFrame.getInstance().getAppCore().loadForeignKeys(ime);

                    List<String> zaFrom = new ArrayList<>();
                    zaFrom = MainFrame.getInstance().getAppCore().loadForeignKeys(imeTabele);
                   // System.out.println(ime);
                    System.out.println(zaFrom);
                    System.out.println(zaJoin);

                    boolean provera = false;
                    for (String text : podaci) {
                        if (text.contains("on")) {
                            provera=true;

                        }
                        if (provera) {
                            if (text.contains(".")) {
                                String[] raz = text.split("\\.");
                                if (!zaJoin.contains(ukloni(raz[1]))){
                                    if (!zaFrom.contains(ukloni(raz[1]))){
                                        System.out.println(raz[1]);
                                        errAppend("strani kljuc", ukloni(text)+"1");
                                        break;
                                    }
                                }

                                if (!zaFrom.contains(ukloni(raz[1]))){
                                    if (!zaJoin.contains(ukloni(raz[1]))){
                                        errAppend("strani kljuc", ukloni(text)+"2");
                                        break;
                                    }
                                }
                            }
                        }

                    }

                    break;
                }
            }



            if (imaAgregat && colone.size() > koliko){
                boolean imaG = false;
                boolean imaB = false;
                for (String tyu: podaci){
                    if (tyu.equals("group")) {
                        imaG = true;
                    }
                    if (tyu.equals("by")) {
                        imaB = true;
                    }
                }
                if (!imaG && !imaB) {
                    errAppend("group");
                }

            }
        }

        // System.out.println(imeTabele);
        // System.out.println(colone);
        // System.out.println(imenaKolona);


    }

    private String ukloniBezZagrada(String staro) {
        return staro.trim().replaceAll(";", "").replaceAll("=", "").replaceAll(",", "").replaceAll("\\)", "").replaceAll("'", "");
    }


    private String ukloni(String staro) {
        return staro.trim().replaceAll(";", "").replaceAll("=", "").replaceAll(",", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("'", "");
    }


}
