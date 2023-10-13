package app;

import database.QueryChecker;
import database.Validator;
import gui.MainFrame;
import utils.ParserCSV;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {

       // System.out.println(PrettySql.run("Select where select where"));

        AppCore appCore = new AppCore();
        MainFrame mainFrame = MainFrame.getInstance();
        mainFrame.setAppCore(appCore);
/*
        Validator k = new Validator("select asd asd  from dsa;");
        if (k.goValidate()) {
            QueryChecker qe = new QueryChecker(k.getFormat());
            if (qe.goQueryCheck()) {
                System.out.println("PROSO");
            }
        }

 */
        /*
        List<String> list = new ArrayList<String>();
        Matcher m = Pattern.compile("([^\']\\S*|\'.+?\')\\s*").matcher("UPDATE jobs SET ContactName = 'Alfred Schmidt', City= 'Frankfurt' WHERE CustomerID = 1;");
        while (m.find())
            list.add(m.group(1));

        for (String s : list){
            System.out.println(s);
        }

        */


        //System.out.println(Arrays.toString("UPDATE jobs SET ContactName = 'Alfred Schmidt', City= 'Frankfurt' WHERE CustomerID = 1;".split("'")));


      //  System.out.println(appCore.loadFunctionNames());

/*
        try {
            File myObj = new File("reci.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
*/

       // ParserCSV par = new ParserCSV("testt.csv", "jobs", appCore.loadColNames("jobs"));

    }

}
