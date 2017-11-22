import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Created by bartek on 03.08.2017.
 */
public class Find extends JFrame {

    private JFrame frameFind = new JFrame("Find");
    private JButton next;
    private JButton previous;
    private JTextField patternField;
    private JPanel panel;
    private int i;
    private TextEditor textEditor;
    private JLabel jLabel;
    private ArrayList<Integer> pos;
    private String text;
    private String pattern;


    public Find(TextEditor textEditor){
        this.textEditor = textEditor;
        pos = new ArrayList<>();

        setLayout(new GridLayout(2,3));
        next = new JButton("next");
        previous = new JButton("previous");
        patternField = new JTextField();
        frameFind.setSize(240,240);
        jLabel = new JLabel("Not found");

        panel = new JPanel();

        frameFind.setContentPane(panel);

        add(patternField);
        add(jLabel);
        add(previous);
        add(next);

        pack();
        frameFind.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);


        next.setEnabled(false);
        previous.setEnabled(false);

        patternField.addKeyListener(k1);


        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textEditor.highlighter.removeAllHighlights();
                if(i<=pos.size()-1){
                    //System.out.println("POS: "+pos.get(i));
                    textEditor.selectText(pos.get(i), pos.get(i)+patternField.getText().length(),  false);
                    i++;
                }else if(i>=pos.size()-1){
                    i=0;
                    textEditor.selectText(pos.get(i), pos.get(i)+patternField.getText().length(), false);
                    i++;
                }
                jLabel.setText(i+" of "+pos.size());
            }
        });

        previous.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textEditor.highlighter.removeAllHighlights();
                if(i==0){
                    i=pos.size()-1;
                    textEditor.selectText(pos.get(i), pos.get(i)+patternField.getText().length(),false);
                }else if(i>0){
                    i--;
                    textEditor.selectText(pos.get(i), pos.get(i)+patternField.getText().length(), false);
                }
                jLabel.setText(i+1+" of "+pos.size());
            }
        });

    }

    private KeyListener k1 = new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            if(patternField.getText().length()>=1){
                findMethod();
            }else textEditor.highlighter.removeAllHighlights();
        }
    };

    private void findMethod(){
        textEditor.highlighter.removeAllHighlights();
        i=0;

        next.setEnabled(false);
        previous.setEnabled(false);

        FindMethod findMethod = new FindMethod(textEditor.area.getText(), patternField.getText());
        pos = findMethod.getPos();

        for(int i=0; i<pos.size(); i++){
            textEditor.selectText(pos.get(i), pos.get(i)+patternField.getText().length(), true);
        }

        //System.out.println(pos);
        jLabel.setText(pos.size()+"");

        if(pos.size()>1){
            next.setEnabled(true);
            previous.setEnabled(true);
        }else{
            next.setEnabled(false);
            previous.setEnabled(false);
        }

    }
}
