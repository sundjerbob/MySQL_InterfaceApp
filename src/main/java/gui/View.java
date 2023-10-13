package gui;

import javax.swing.*;
import java.awt.*;

public class View extends JPanel {

    private MenuBar menuBar;

    public View(JTable jTable, JPanel left) {
        setLayout(new BorderLayout());
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.add(MainFrame.getInstance().getActionManager().getEditAction());
        add(tb, BorderLayout.NORTH);
        add(left, BorderLayout.WEST);
        add(new JScrollPane(jTable), BorderLayout.CENTER);
        menuBar = new MenuBar();
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }
}
