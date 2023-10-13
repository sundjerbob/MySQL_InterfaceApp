package gui;

import gui.actions.PrettyAction;
import utils.PrettySql;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;


public class Edit extends JPanel {

    private final InputPanel inputPanel;
    private final StackTracePanel stPanel;

    public Edit() {
        setLayout(new BorderLayout());
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(MainFrame.getInstance().getActionManager().getViewAction());
        toolBar.add(MainFrame.getInstance().getActionManager().getRunAction());
        JButton button = new JButton(MainFrame.getInstance().getActionManager().getPrettyAction()){
            @Override
            public void paint(Graphics g) {
                if(((PrettyAction) getAction()).isOn())
                    setBackground(Color.cyan.darker());
                else
                    setBackground(Color.white);
                super.paint(g);
            }
        };
        button.setOpaque(true);
        button.setFocusPainted(false);
        toolBar.add(button);

        add(toolBar, BorderLayout.NORTH);
        stPanel = new StackTracePanel();

        inputPanel = new InputPanel();
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,inputPanel,stPanel);
        split.setDividerLocation(MainFrame.getInstance().getHeight()/3);
        add(split,BorderLayout.CENTER);
    }

    public StackTracePanel getStPanel() {
        return stPanel;
    }

    public InputPanel getInputPanel() {
        return inputPanel;
    }

    public  class InputPanel extends JPanel{

        private final JTextPane input;
        private String inputBuff;
        boolean isPretty;

        protected InputPanel(){
            setLayout(new BorderLayout());
            input = new JTextPane();
            input.setContentType("text/plain");
            add(input,BorderLayout.CENTER);
            isPretty = false;
        }

        public boolean pretty() {
            if (isPretty) {
                input.setContentType("text/plain");
                input.setText(inputBuff);
                input.updateUI();
                return isPretty = false;
            }
            inputBuff = input.getText();
            input.setContentType("text/html");
            input.setText(PrettySql.run(inputBuff));
            input.updateUI();

            return isPretty = true;
        }

        public String getText(){
            if(!isPretty)
                inputBuff = input.getText();
            if(inputBuff == null)
                return "";
            return inputBuff;
        }

        public boolean isPretty() {
            return isPretty;
        }
    }


    public class StackTracePanel extends JPanel {

        private final ArrayList<String> stacktrace;
        private  int brojac;
        protected BoxLayout box;
        protected StackTracePanel(){
            stacktrace = new ArrayList<>();
            setBackground(Color.yellow);
            setLayout(null);
            setSize(MainFrame.getInstance().getWidth(),500);
        }
        public void showStackTrace(String s){
            stacktrace.add(s);
            brojac++;
            if(stacktrace.size() > 5)
                stacktrace.remove(0);
            removeAll();
            JLabel curr;
            for(int i = 0; i < stacktrace.size(); i++){
                curr = new JLabel( brojac - stacktrace.size() + i + 1 + ".   " + stacktrace.get(i));
                curr.setBounds(30, 15 + i * 25,MainFrame.getInstance().getWidth(),25);
                add(curr);

            }
            updateUI();
        }
    }

}
