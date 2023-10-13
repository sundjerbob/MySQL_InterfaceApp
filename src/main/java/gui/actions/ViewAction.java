package gui.actions;

import gui.MainFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ViewAction extends Action{

    public ViewAction(){
        putValue(LARGE_ICON_KEY, loadIcon("images/table.png"));
        putValue(NAME,"view");
        putValue(SHORT_DESCRIPTION,"view tables");
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V,ActionEvent.SHIFT_MASK,true));
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        MainFrame.getInstance().goView();
    }
}
