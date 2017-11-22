
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by bartek on 02.08.2017.
 */
public class TextEditor extends JFrame {



    public JTextArea area = new JTextArea(20,20);
    private JFileChooser dialog = new JFileChooser(System.getProperty("user.dir"));
    private FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt", "text");
    private JCheckBoxMenuItem jCheckBoxMenuItem;
    private String currentFile = "Untitled";
    private JLabel info;
    private Toolkit kit = Toolkit.getDefaultToolkit();
    private Dimension screenSize = kit.getScreenSize();
    private int screenWidth = screenSize.width;
    private int screenHeight = screenSize.height;
    private boolean changed = false;
    private ImageIcon img;


    public Highlighter highlighter;
    private Highlighter.HighlightPainter redPainter;
    private Highlighter.HighlightPainter orangePainter;
    private Find find;
    private ChangeFont changeFont;
    private FindAndReplace findAndReplace;


    public TextEditor(){


        setSize(screenWidth/2, screenHeight/2);
        setLocationByPlatform(true);

        img = new ImageIcon(getClass().getResource("img/cut.gif"));
        //setIconImage(img.getImage());
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/icon.gif")));

        redPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
        orangePainter = new DefaultHighlighter.DefaultHighlightPainter(Color.ORANGE);


        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setCaretColor(Color.blue);
        area.setVisible(true);
        JScrollPane scroll = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scroll, BorderLayout.CENTER);


        JMenuBar JMB = new JMenuBar();
        setJMenuBar(JMB);
        JMenu file = new JMenu("File");
        JMenu edit = new JMenu("Edit");
        JMenu help = new JMenu("Help");
        JMB.add(file); JMB.add(edit); JMB.add(help);
        jCheckBoxMenuItem = new JCheckBoxMenuItem("Wrap Line");
        jCheckBoxMenuItem.addActionListener(k2);

        info = new JLabel("0");


        file.add(New);
        file.add(Open);
        file.add(Save);
        file.add(Quit);
        file.add(SaveAs);
        file.addSeparator();

        for(int i=0; i<4; i++){
            file.getItem(i).setIcon(null);
        }

        edit.add(Cut);
        edit.add(Copy);
        edit.add(Paste);
        edit.addSeparator();
        edit.add(SelectAll);
        edit.addSeparator();
        edit.add(Fonts);
        edit.addSeparator();
        edit.add(Find);
        edit.add(Replace);
        edit.add(jCheckBoxMenuItem);
        edit.getItem(0).setText("Cut Out");
        edit.getItem(1).setText("Copy");
        edit.getItem(2).setText("Paste");

        JToolBar tool = new JToolBar();
        add(tool, BorderLayout.NORTH);
        tool.add(New);
        tool.add(Open);
        tool.add(Save);
        tool.addSeparator();

        help.add(About);
        help.getItem(0).setText("About");

        add(info, BorderLayout.SOUTH);


        JButton cut = tool.add(Cut), cop = tool.add(Copy), pas = tool.add(Paste);

        cut.setText(null); cut.setIcon(new ImageIcon(getClass().getResource("img/cut.gif")));
        cop.setText(null); cop.setIcon(new ImageIcon(getClass().getResource("img/copy.gif")));
        pas.setText(null); pas.setIcon(new ImageIcon(getClass().getResource("img/paste.gif")));

        Save.setEnabled(false);
        SaveAs.setEnabled(false);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //pack();
        area.addKeyListener(k1);
        setTitle(currentFile);


        setVisible(true);

        highlighter = area.getHighlighter();

        //dialog.setAcceptAllFileFilterUsed(false);
        dialog.setFileFilter(filter);

        find = new Find(this);
        changeFont = new ChangeFont(this);
        findAndReplace = new FindAndReplace(this);

    }

    public void selectText(int s, int l, boolean all){
        if(all){
            try {
                highlighter.addHighlight(s, l, redPainter);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }else{
            try {
                highlighter.addHighlight(s, l, orangePainter);
                area.setCaretPosition(l);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

    }

    private KeyListener k1 = new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            info.setText(area.getText().length()+"");
        }

        @Override
        public void keyPressed(KeyEvent e) {
            changed = true;
            Save.setEnabled(true);
            SaveAs.setEnabled(true);
            highlighter.removeAllHighlights();
        }
    };

    private ActionListener k2 = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(jCheckBoxMenuItem.isSelected()){
                area.setLineWrap(true);
                area.setWrapStyleWord(true);
            }else{
                area.setLineWrap(false);
                area.setWrapStyleWord(false);
            }

        }
    };

    Action About = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(null, "Autor: Bartłomiej Nems" +
                    "\ne-mail: barteknems@gmail.com \nversion: 1.0", "About", JOptionPane.INFORMATION_MESSAGE);
        }
    };


    Action New = new AbstractAction("New") {
        @Override
        public void actionPerformed(ActionEvent e) {
            newFile();
        }
    };

    Action Open = new AbstractAction("Open", new javax.swing.ImageIcon(getClass().getResource("img/open.gif"))) {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(dialog.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                readInFile(dialog.getSelectedFile().getAbsolutePath());
            }
            SaveAs.setEnabled(true);
            info.setText(area.getText().length()+"");
        }
    };

    Action Save = new AbstractAction("Save", new javax.swing.ImageIcon(getClass().getResource("img/save.gif"))) {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(!currentFile.equals("Untitled")) saveFile(currentFile);
            else saveFileAs();
        }
    };

    Action SaveAs = new AbstractAction("Save as..") {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveFileAs();
        }
    };

    Action Quit = new AbstractAction("Quit") {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveOld();
            System.exit(0);
        }
    };

     Action Find = new AbstractAction("Find") {
        @Override
        public void actionPerformed(ActionEvent e) {
            find.setVisible(true);
        }
    };

    Action Replace = new AbstractAction("Replace") {
        @Override
        public void actionPerformed(ActionEvent e) {
            findAndReplace.setVisible(true);
        }
    };

     Action Fonts = new AbstractAction("Fonts") {
         @Override
         public void actionPerformed(ActionEvent e) {
             changeFont.setVisible(true);
         }
     };

    Action SelectAll = new AbstractAction("Select All") {
        @Override
        public void actionPerformed(ActionEvent e) {
            area.selectAll();
        }
    };

    ActionMap m = area.getActionMap();
    Action Cut = m.get(DefaultEditorKit.cutAction);
    Action Copy = m.get(DefaultEditorKit.copyAction);
    Action Paste = m.get(DefaultEditorKit.pasteAction);

    private void newFile(){
        if(changed){
            if(JOptionPane.showConfirmDialog(this, "Would you like to save "+currentFile+" ?", "Save", JOptionPane.YES_NO_OPTION)
                    == JOptionPane.YES_OPTION){
                if(currentFile.equals("Untitled")){
                    saveFileAs();
                }else saveFile(currentFile);
            }
        }
        currentFile = "Untitled";
        setTitle(currentFile);
        area.selectAll();
        area.replaceSelection("");
        info.setText(area.getText().length()+"");
    }

    private void saveFileAs(){
        if(dialog.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
            saveFile(dialog.getSelectedFile().getAbsolutePath());
    }

    private void saveOld(){
        if(changed){
            if(JOptionPane.showConfirmDialog(this, "Would you like to save "+currentFile+" ?", "Save", JOptionPane.YES_NO_OPTION)
                    == JOptionPane.YES_OPTION)
                saveFile(currentFile);
        }
    }

    private void readInFile(String fileName){
        try{
            FileReader r = new FileReader(fileName);
            area.read(r, null);
            r.close();
            currentFile = fileName;
            setTitle(currentFile);
            changed = false;
        }catch (IOException e){
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "Editor can`t find the file called "+ fileName);
        }
    }

    private void saveFile(String fileName){
        FileWriter w = null;
        try {
            w = new FileWriter(fileName);
            area.write(w);
            w.close();
            currentFile = fileName;
            setTitle(currentFile);
            changed = false;
            Save.setEnabled(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args){
        TextEditor textEditor  = new TextEditor();
        //Find find = new Find(textEditor);
        //find.setVisible(true);

    }

}