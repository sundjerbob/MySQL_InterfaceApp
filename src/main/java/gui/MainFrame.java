package gui;

import app.AppCore;
import gui.actions.ActionManager;
import lombok.Data;
import observer.Notification;
import observer.Subscriber;
import tree.implementation.SelectionListener;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

@Data
public class MainFrame extends JFrame  {

    private static MainFrame instance = null;

    private AppCore appCore;
    private JTable jTable;
    private View view;
    private Edit edit;
    private JTree jTree;
    private JPanel left;
    private ActionManager actionManager;

    private MainFrame() {

    }

    public static MainFrame getInstance(){
        if (instance==null){
            instance=new MainFrame();
            instance.initialise();
        }
        return instance;
    }


    private void initialise() {

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jTable = new JTable();
        jTable.setPreferredScrollableViewportSize(new Dimension(500, 400));
        jTable.setFillsViewportHeight(true);
        actionManager = new ActionManager();
        setSize(new Dimension(600,500));
        this.setLocationRelativeTo(null);
        this.setVisible(true);

    }

    public void setAppCore(AppCore appCore) {
        this.appCore = appCore;
        this.jTable.setModel(appCore.getTableModel());
        initialiseTree();
        jTree.setRootVisible(false);
        view = new View(jTable, left);
        edit = new Edit();
        goView();
    }

    private void initialiseTree() {
        DefaultTreeModel defaultTreeModel = appCore.loadResource();
        jTree = new JTree(defaultTreeModel);
        jTree.addTreeSelectionListener(new SelectionListener());
        JScrollPane jsp = new JScrollPane(jTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        left = new JPanel(new BorderLayout());
        left.add(jsp, BorderLayout.CENTER);


    }

    public void goView(){
       if(view == null)
           return;
       remove(edit);
       add(view);
       setJMenuBar(view.getMenuBar());
       pack();
       view.updateUI();
    }
    public void goEdit(){
      if(edit == null)
          return;
      setJMenuBar(null);
      remove(view);
      add(edit);
      edit.updateUI();
    }

    public JTree getjTree() {
        return jTree;
    }

}
