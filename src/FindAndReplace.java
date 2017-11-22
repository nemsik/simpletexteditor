import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Created by bartek on 09.08.2017.
 */
public class FindAndReplace extends JFrame {
    private JLabel jTextR = new JLabel("Text");
    private JLabel jPatternR = new JLabel("Pattern");
    private JLabel jInfoR = new JLabel("Not found");
    private JTextField fieldTextR = new JTextField();
    private JTextField fieldPatternR = new JTextField();
    private JButton replaceR = new JButton("Replace");
    private JButton nextR = new JButton("next");
    private JButton previousR = new JButton("previous");
    private JCheckBox replaceAllR = new JCheckBox("Replace all");
    private JProgressBar jProgressBar;

    private String textR;
    private String patternR;
    private String textDoc;
    private ArrayList<Integer> posR = new ArrayList<>();
    private int i=0;

    private TextEditor textEditor;


    public FindAndReplace(TextEditor textEditor){
        this.textEditor = textEditor;

        jProgressBar = new JProgressBar();
        jProgressBar.setValue(0);

        setLayout(new GridLayout(5,2));
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setTitle("Find and Replace");

        add(jPatternR);
        add(fieldPatternR);
        add(jTextR);
        add(fieldTextR);


        add(replaceR);
        add(replaceAllR);
        add(previousR);
        add(nextR);

        add(jInfoR);

        add(jProgressBar);

        pack();

        replaceR.setEnabled(false);
        replaceAllR.setEnabled(false);
        nextR.setEnabled(false);
        previousR.setEnabled(false);


        fieldPatternR.addKeyListener(k1R);
        replaceR.addActionListener(b1R);
        //replaceAllR.addActionListener(c1R);
        nextR.addActionListener(n1R);
        previousR.addActionListener(p1R);



    }


    private KeyListener k1R = new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            if(fieldPatternR.getText().length()>=1){
                findMethodR();
            }else textEditor.highlighter.removeAllHighlights();
        }

    };



    private ActionListener b1R  = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    try {

                        jProgressBar.setValue(0);

                        patternR = fieldPatternR.getText();
                        patternR = patternR.toUpperCase();

                        textR = fieldTextR.getText();

                        int patternRlenght = patternR.length();
                        int textRlenght = textR.length();

                        if (replaceAllR.isSelected()) {
                            Document doc = textEditor.area.getDocument();

                            textDoc = doc.getText(0, doc.getLength());
                            textDoc = textDoc.toUpperCase();

                            int offset = 0;
                            int count = 0;
                            float progrss = 0.0f;

                            while ((offset = textDoc.indexOf(patternR, offset)) != -1) {
                                //System.out.println(count);
                                int replaceOffset = offset + ((textRlenght - patternRlenght) * count);
                                textEditor.area.replaceRange(textR, replaceOffset, replaceOffset+patternRlenght);
                                //textEditor.area.select(replaceOffset, replaceOffset + patternRlenght);
                                //textEditor.area.replaceSelection(textR);
                                offset += patternRlenght;
                                count++;
                                System.out.println(((float) count / posR.size()) * 100 + "%");
                                progrss = (float) count / posR.size() * 100;
                                jProgressBar.setValue((int)progrss);
                            }
                            replaceR.setEnabled(false);
                            replaceAllR.setEnabled(false);
                            nextR.setEnabled(false);
                            previousR.setEnabled(false);
                            fieldTextR.setEnabled(false);

                        } else {

                            if (posR.size() == 1 || i == 0) {
                                textEditor.area.select(posR.get(i), posR.get(i) + patternRlenght);
                                textEditor.area.replaceSelection(textR);
                                textEditor.area.setCaretPosition(posR.get(i) + patternRlenght);
                            } else {
                                textEditor.area.select(posR.get(i - 1), posR.get(i - 1) + patternRlenght);
                                textEditor.area.replaceSelection(textR);
                                textEditor.area.setCaretPosition(posR.get(i - 1) + patternRlenght);
                            }

                        }

                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }

                    findMethodR();

                    return null;
                }

                @Override
                protected void done() {
                    jInfoR.setText("Done");
                }
            };
            worker.execute();

        }
    };

    private ActionListener n1R = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            textEditor.highlighter.removeAllHighlights();
            if(i<=posR.size()-1){
                textEditor.selectText(posR.get(i), posR.get(i)+patternR.length(), false);
                i++;
            }else if(i>=posR.size()-1){
                i=0;
                textEditor.selectText(posR.get(i), posR.get(i)+patternR.length(), false);
                i++;
            }
            jInfoR.setText(i+" of "+posR.size());
        }
    };

    private ActionListener p1R = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            textEditor.highlighter.removeAllHighlights();
            if(i==0){
                i=posR.size()-1;
                textEditor.selectText(posR.get(i), posR.get(i)+patternR.length(),false);
            }else if(i>0){
                i--;
                textEditor.selectText(posR.get(i), posR.get(i)+patternR.length(), false);
            }
            jInfoR.setText(i+1+" of "+posR.size());
        }
    };


    private void findMethodR(){
        textEditor.highlighter.removeAllHighlights();


        i=0;


        replaceR.setEnabled(false);
        replaceAllR.setEnabled(false);
        nextR.setEnabled(false);
        previousR.setEnabled(false);
        fieldTextR.setEnabled(false);

        textR = textEditor.area.getText().toUpperCase();
        patternR = fieldPatternR.getText().toUpperCase();

        FindMethod findMethod = new FindMethod(textEditor.area.getText(), fieldPatternR.getText());
        posR = findMethod.getPos();

        for(int i=0; i<posR.size(); i++){
            textEditor.selectText(posR.get(i), posR.get(i)+fieldPatternR.getText().length(), true);
        }


        if(posR.size()>1){
            replaceR.setEnabled(true);
            replaceAllR.setEnabled(true);
            nextR.setEnabled(true);
            previousR.setEnabled(true);
            fieldTextR.setEnabled(true);
        }else if(posR.size()==1){
            replaceR.setEnabled(true);
        }
        else{
            replaceR.setEnabled(false);
            replaceAllR.setEnabled(false);
            previousR.setEnabled(false);
            fieldTextR.setEnabled(false);
        }

        if(posR.size()>0){
            jInfoR.setText("Found: "+ posR.size());
        }else{
            jInfoR.setText("Not Found");
        }


    }

}
