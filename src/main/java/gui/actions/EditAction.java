package gui.actions;

import gui.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class EditAction extends Action {

    public EditAction(){

        putValue(LARGE_ICON_KEY, loadIcon("images/edit.png"));
        putValue(NAME,"edit");
        putValue(SHORT_DESCRIPTION,"Edit tables");
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E,ActionEvent.SHIFT_MASK,true));

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        MainFrame.getInstance().goEdit();
    }
}
