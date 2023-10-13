package gui.popups;

import gui.MainFrame;
import utils.SaveCSV;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ExportPopup extends JDialog {


    private JFileChooser fileChooser;
    private JTextField path;

    public ExportPopup(TreeNode node) {

        fileChooser = new JFileChooser();
        path = new JTextField();

        JPanel content = new JPanel();
        content.setLayout(null);
        content.setPreferredSize(new Dimension(670, 600));

        JLabel table = new JLabel("Export file into " + node.toString() + " table.");
        table.setHorizontalAlignment(SwingConstants.CENTER);

        JButton cancel = new JButton("Cancel");
        JButton exp = new JButton("Export");
        JButton chooseFile = new JButton("Choose File");
        JLabel fLabel = new JLabel("Enter file path: ");

        cancel.setBounds(70, 370, 130, 40);
        exp.setBounds(70 + cancel.getWidth() + 70, 370, 140, 40);
        fLabel.setBounds(70, 240, 150, 40);
        path.setBounds(70 + fLabel.getWidth() + 10, 240, 250, 40);
        chooseFile.setBounds(70 + fLabel.getWidth() + 10 + path.getWidth() + 20, 240, 150, 40);
        table.setBounds(50, 100, 500, 40);

        content.add(cancel);
        content.add(exp);
        content.add(chooseFile);
        content.add(fLabel);
        content.add(path);
        content.add(table);

        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        chooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = fileChooser.showOpenDialog(ExportPopup.this);
                if (response == JFileChooser.APPROVE_OPTION) {
                    path.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    ExportPopup.this.repaint();
                }
            }
        });

        exp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {/////////////odradjeno
                if (path.getText().length() == 0)
                    JOptionPane.showMessageDialog(ExportPopup.this, "You need to enter file path.");

                else {
                    // File f = new File(path.getText());
                    //System.out.println(path.getText());
                    SaveCSV s = new SaveCSV(MainFrame.getInstance().getAppCore().getDatabase().loadResSet(node.toString()), path.getText());
                    if (s.isStatus()) {
                        JOptionPane.showMessageDialog(ExportPopup.this, "Uspesno!");
                    } else {
                        JOptionPane.showMessageDialog(ExportPopup.this, "lose formatiran csv");
                    }
                }
            }

        });


        setContentPane(content);
        pack();
        setModal(true);
        setVisible(true);


    }
}
