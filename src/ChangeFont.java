import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/**
 * Created by bartek on 09.08.2017.
 */
public class ChangeFont extends JFrame {
    GraphicsEnvironment ge;
    private JFrame frame = new JFrame("Change font");
    private JPanel panel;
    private String[] fonts;
    private Integer[] size = {4,8,10,12, 14, 16, 18, 20, 24, 30, 34, 42};
    private JComboBox fontChooser;
    private JComboBox<Integer> sizeChooser;
    private TextEditor textEditor;

    public ChangeFont(TextEditor textEditor){
        this.textEditor = textEditor;
        panel = new JPanel();
        frame.setContentPane(panel);
        ge = GraphicsEnvironment.
                getLocalGraphicsEnvironment();
        fonts = ge.getAvailableFontFamilyNames();
        fontChooser = new JComboBox(fonts);
        sizeChooser =  new JComboBox<>(size);
        setLayout(new BorderLayout());


        add(fontChooser, BorderLayout.WEST);
        add(sizeChooser, BorderLayout.EAST);
        pack();
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        fontChooser.addActionListener(k1);
        sizeChooser.addActionListener(k1);


    }

    ActionListener k1 = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println(fontChooser.getSelectedItem()+ " " + sizeChooser.getSelectedItem());
            Font font = new Font((String)fontChooser.getSelectedItem(),
                    Font.PLAIN, (Integer)sizeChooser.getSelectedItem());
            System.out.println(font);
            textEditor.area.setFont(font);

        }
    };

}
