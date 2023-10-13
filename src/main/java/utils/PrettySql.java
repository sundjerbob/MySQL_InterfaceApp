package utils;

import resource.KljucnaRec;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class PrettySql {

    private static boolean prazno(String s) {
        return s == null || s.length() == 0;
    }

    private static int prebroji(String text, String str) {
        //  text = text.toLowerCase(Locale.ROOT);

        str = str.toLowerCase(Locale.ROOT);

        // FIXME mozda je lose da stoji razmak ali tako se uklanja greksa ako su reci tipa "promenjiva_select"
        if (prazno(text) || prazno(str)) {
            return 0;
        }
        int poz = 0, br = 0;
        while (true) {
            poz = text.indexOf(str, poz);
            if (poz != -1) {
                br++;
                poz += str.length();
            } else
                break;
        }
        return br;
    }

    public static ArrayList<KljucnaRec> ucitajKomplex() {
        ArrayList<KljucnaRec> reci = new ArrayList<>();
        try {
            FileReader file = new FileReader("reci.txt");
            Scanner s = new Scanner(file);
            while (s.hasNextLine()) {
                String rec = s.nextLine();
                if (!rec.equals("")) {
                    String[] r = rec.split(":");
                    reci.add(new KljucnaRec(r[0], r[1].equals("1")));
                }

            }

        } catch (FileNotFoundException e) {
            System.out.println("Greska u ucitavanju.");
        }
        return reci;
    }

    public static ArrayList<String> ucitaj() {
        ArrayList<String> reci = new ArrayList<>();
        try {
            FileReader file = new FileReader("reci.txt");
            Scanner s = new Scanner(file);
            while (s.hasNextLine()) {
                String rec = s.nextLine();
                if (!rec.equals("")) {
                    String[] r = rec.split(":");
                    reci.add(r[0]);
                }

            }

        } catch (FileNotFoundException e) {
            System.out.println("Greska u ucitavanju.");
        }
        return reci;
    }

    public static String run(String upit) {
        ArrayList<KljucnaRec> reci = ucitajKomplex();

        String resenje = upit.toLowerCase().trim().replaceAll(" +", " ");
        resenje = srediZareze(resenje);
        for (KljucnaRec s : reci) {
            int poz = -1;
            int br = prebroji(resenje, s.getRec());
            for (int i = 0; i < br; i++) {
                int next = resenje.indexOf(s.getRec(), poz + 1);
                String asd = resenje.substring(0, next);
                if (s.isSledeciRed()) {
                    asd += "<br>";
                }
                asd += "<FONT COLOR=BLUE>" + s.getRec().toUpperCase() + "</FONT>" + resenje.substring(next + s.getRec().length()); // FIXME zasto ne radi kada se stavi mali tekst html-a
                next = asd.indexOf(s.getRec(), poz + 1);
                poz = next - 1;
                resenje = asd;
                System.out.println(resenje);
            }

        }
        if (resenje.startsWith("<br>")) {
            resenje = resenje.substring("<br>".length());
        }

        return "<html>" + resenje + "</html>";
    }

    public static String srediZareze(String upit) {
        String resenje = upit.toLowerCase().trim().replaceAll(" +", " ");
        int poz = -1;
        int br = prebroji(resenje, ",");
        for (int i = 0; i < br; i++) {
            int next = resenje.indexOf(",", poz + 1);
            String asd = "";

            String pre = resenje.substring(next - 1, next);
            String posle = resenje.substring(next+ 2 > resenje.length()?next :next + 1, next+ 2 > resenje.length()?next + 1 :next + 2);
            int preV = 0;
            if (pre.equals(" ")) {
                preV = 1;
            }
            int posleV = 0;
            if (posle.equals(" ")) {
                posleV = 1;
            }
            asd += resenje.substring(0, next - preV) + ", " + resenje.substring(next + ",".length() + posleV);
            next = asd.indexOf(",", poz + 1);
            poz = next;
            resenje = asd;

        }
        return resenje;
    }
}
