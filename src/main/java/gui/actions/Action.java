package gui.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.URL;

public abstract class Action extends AbstractAction {


        protected Icon loadIcon(String fileName){
            URL imageURL = getClass().getResource(fileName);
            Icon icon = null;

            if(imageURL != null)
                icon = new ImageIcon(imageURL);
            else
                System.out.println("MyAbstractAction - load icon failed");
            return icon;
        }


}
