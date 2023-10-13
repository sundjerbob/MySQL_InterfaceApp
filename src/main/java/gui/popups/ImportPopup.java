package gui.popups;

import database.QueryChecker;
import database.Validator;
import gui.MainFrame;
import utils.ParserCSV;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ImportPopup extends JDialog {

    private JFileChooser fileChooser;
    private JTextField path;

    public ImportPopup(TreeNode node) {

        fileChooser = new JFileChooser();
        path = new JTextField();

        JPanel content = new JPanel();
        content.setLayout(null);
        content.setPreferredSize(new Dimension(670, 600));

        JLabel table = new JLabel("Import file into " + node.toString() + " table.");
        table.setHorizontalAlignment(SwingConstants.CENTER);

        JButton cancel = new JButton("Cancel");
        JButton imp = new JButton("Import");
        JButton chooseFile = new JButton("Choose File");
        JLabel fLabel = new JLabel("Enter file path: ");

        cancel.setBounds(70, 370, 130, 40);
        imp.setBounds(70 + cancel.getWidth() + 70, 370, 140, 40);
        fLabel.setBounds(70, 240, 150, 40);
        path.setBounds(70 + fLabel.getWidth() + 10, 240, 250, 40);
        chooseFile.setBounds(70 + fLabel.getWidth() + 10 + path.getWidth() + 20, 240, 150, 40);
        table.setBounds(50, 100, 500, 40);

        content.add(cancel);
        content.add(imp);
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
                int response = fileChooser.showOpenDialog(ImportPopup.this);
                if (response == JFileChooser.APPROVE_OPTION) {
                    path.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    ImportPopup.this.repaint();
                }
            }
        });

        imp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {/////////////Fixme prilagoditi
                if (path.getText().length() == 0)
                    JOptionPane.showMessageDialog(ImportPopup.this, "You need to enter file path.");

                else {
                    //  File f = new File(path.getText());
                    //   System.out.println("file ");
                    ParserCSV par = new ParserCSV(path.getText(), node.toString(), MainFrame.getInstance().getAppCore().loadColNames(node.toString()));
                    if (par.isError()) {
                        JOptionPane.showMessageDialog(ImportPopup.this, "lose formatiran csv");
                    } else {
                        boolean pret = true;
                        for (String s : par.getInsert()) {
                             System.out.println(s);
                            Validator k = new Validator(s);
                            if (k.goValidate()) {
                                QueryChecker qe = new QueryChecker(k.getFormat());
                                if (qe.goQueryCheck()) {

                                    int odgovor = MainFrame.getInstance().getAppCore().loadExecUpdate(qe.getQuery());
                                    if (odgovor != 1) {
                                        pret = false;
                                    }

                                }
                            }
                        }

                        if (pret)
                            JOptionPane.showMessageDialog(ImportPopup.this, "Uspesno!");
                        else
                            JOptionPane.showMessageDialog(ImportPopup.this, "Greska na serveru.");
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
