package gui.actions;



import gui.MainFrame;


import java.awt.event.ActionEvent;


public class PrettyAction extends Action{
    private boolean isOn;

    public PrettyAction(){
        isOn = false;
        putValue(LARGE_ICON_KEY, loadIcon("images/edit.png"));
        putValue(NAME,"pretty");
        putValue(SHORT_DESCRIPTION,"make query pretty");


    }
    @Override
    public void actionPerformed(ActionEvent e) {
       isOn = MainFrame.getInstance().getEdit().getInputPanel().pretty();
    }

    public boolean isOn() {
        return isOn;
    }
}
