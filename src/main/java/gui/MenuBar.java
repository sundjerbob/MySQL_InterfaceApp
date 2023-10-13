package gui;

import gui.popups.ExportPopup;
import gui.popups.ImportPopup;
import resource.DBNodeComposite;
import tree.TreeItem;

import javax.swing.*;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

public class MenuBar extends JMenuBar {

    public MenuBar(){


        JMenu file = new JMenu("File");
        JMenuItem imp = new JMenuItem("Import file");
        imp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreeNode node = (TreeNode) MainFrame.getInstance().getjTree().getLastSelectedPathComponent();
                if(node != null && node.getParent().equals(MainFrame.getInstance().getAppCore().getTree().getRoot()))
                    new ImportPopup(node);
                else
                    JOptionPane.showMessageDialog(null,"You need to select table from the tree.");
            }
        });


        JMenuItem exp = new JMenuItem("Export file");
        exp.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                TreeNode node = (TreeNode) MainFrame.getInstance().getjTree().getLastSelectedPathComponent();
                if(node != null && node.getParent().equals(MainFrame.getInstance().getAppCore().getTree().getRoot()))
                    new ExportPopup(node);
                else
                    JOptionPane.showMessageDialog(null,"You need to select table from the tree.");
            }
        });

        file.add(imp);
        file.add(exp);

        add(file);
        setBackground(Color.GRAY);
    }
}
