package gui.actions;


import database.QueryChecker;
import database.Validator;
import gui.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class RunAction extends Action {
    public RunAction() {
        putValue(LARGE_ICON_KEY, loadIcon("images/edit.png"));
        putValue(NAME, "run");
        putValue(SHORT_DESCRIPTION, "Edit tables");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(MainFrame.getInstance().getEdit().getInputPanel().isPretty())
            return;
        String query = MainFrame.getInstance().getEdit().getInputPanel().getText();
        //System.out.println(query + "MILAN");
        Validator k = new Validator(query);
        if (k.goValidate()) {
            QueryChecker qe = new QueryChecker(k.getFormat());
            if (qe.goQueryCheck()) {
                if (qe.getQuery().contains("select")) {
                    MainFrame.getInstance().getAppCore().loadGetQuery(qe.getQuery());
                    MainFrame.getInstance().goView();
                } else {
                    int odgovor = MainFrame.getInstance().getAppCore().loadExecUpdate(qe.getQuery());
                    String opis = "Greska u izvrsavanju na serveru!";
                    if (odgovor != -200) {
                        opis = "Uspesno izvrseno!";
                    }
                    JOptionPane.showMessageDialog(null, opis);
                }
            }
        }
    }
}
