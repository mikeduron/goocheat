package ch.pterrettaz.goocheat;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Main extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final int MAX_LENGTH = 18;
    private static final int MIN_SEARCH_RESULT = 1;
    private static final int MAX_SEARCH_RESULT = 15;
    private final int TAB_SPACE;
    protected final GooCheat gooCheat;
    private final JTextField text;
    private final JProgressBar bar;
    private final JTextArea result;
    private final JSlider slider;
    private final JLabel sliderLabel;
    
    public Main(final GooCheat gooCheat) {
        this.gooCheat = gooCheat;
        String version = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("version.txt")));
            version = in.readLine();
            in.close();
        } catch (Exception e1) {
        }
        setTitle("Goo Cheat - " + version);
        setSize(280, 300);
        Dimension windowDim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(windowDim.width / 2 - getWidth() / 2, windowDim.height / 2 - getHeight() / 2);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        TAB_SPACE = String.valueOf(MAX_SEARCH_RESULT).length()+1;
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 3, 3, 3);
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        text = new JTextField();
        text.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
                JTextField source = (JTextField) e.getSource();
                if (e.getKeyChar() != '\n' && source.getText().length() > MAX_LENGTH-1)
                    source.setText(source.getText().substring(0, MAX_LENGTH-1));
            }

            public void keyReleased(KeyEvent e) {
                performSearch();
            }

            public void keyPressed(KeyEvent e) {
            }
        });
        panel.add(text, c);

        c.weightx = 0;
        c.gridx = 1;
        c.fill = GridBagConstraints.NONE;
        bar = new JProgressBar();
        Dimension minSize = bar.getMinimumSize();
        minSize.width = 50;
        bar.setMinimumSize(minSize);
        bar.setMaximumSize(minSize);
        panel.add(bar, c);
        
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        result = new JTextArea();
        result.setEditable(false);
        result.setFont(new Font("Monospaced", Font.PLAIN, result.getFont().getSize()));
        result.setForeground(Color.GRAY);
        panel.add(new JScrollPane(result), c);
        
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 1;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        slider = new JSlider(MIN_SEARCH_RESULT , MAX_SEARCH_RESULT );
        slider.setOrientation(JSlider.HORIZONTAL);
        slider.setValue(10);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                performSearch();
                sliderLabel.setText(""+slider.getValue());
            }
        });
        panel.add(slider, c);
        
        c.gridx = 1;
        c.gridy = 3;
        c.weightx = 0;
        c.weighty = 0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        sliderLabel = new JLabel(slider.getValue()+"");
        sliderLabel.setForeground(Color.GRAY);
        panel.add(sliderLabel, c);
        
        setContentPane(panel);
        setVisible(true);
        
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.isMetaDown() && e.getKeyChar() == 'w') {
                    Main.this.processWindowEvent(new WindowEvent(Main.this, WindowEvent.WINDOW_CLOSING));
                    return true;
                }
                return false;
            }
        });
    }
    
    private int numberCurrentSearch = 0;
    private synchronized void fireSearchStart() {
        if (++numberCurrentSearch != 0)
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    bar.setIndeterminate(true);
                }
            });
    }
    
    private synchronized void fireSearchEnd() {
        numberCurrentSearch--;
        if (numberCurrentSearch == 0)
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    bar.setIndeterminate(false);
                }
            });
    }
    
    private void performSearch() {
        new Thread(){
            @Override
            public void run() {

                try {
                    final String search = text.getText().trim();
                    if (search.equals("")) {
                        text.setText("");
                        return;
                    }

                    final List<String> ret = new LinkedList<String>();
                    fireSearchStart();
                    ret.addAll(gooCheat.getValidWordsPermutation(search, slider.getValue()));
                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                            try {
                                StringBuilder builder = new StringBuilder();
                                int count = 1;
                                for (String word : ret) {
                                    insertNumerotation(builder, count++);
                                    builder.append(word);
                                    builder.append('\n');
                                }
                                result.setText(builder.toString());
                                result.setCaretPosition(0);
                            } finally {
                                fireSearchEnd();
                            }
                        }

                        private void insertNumerotation(StringBuilder builder, int it) {
                            builder.append(it);
                            int size = TAB_SPACE - String.valueOf(it).length();
                            for (int i = 0; i<size; i++) {
                                builder.append(" ");
                            }
                        }
                    });
                } catch (Exception ex) {
                }
            }
        }.start();
    }
    
    public static void main(String[] args) throws Exception {
        new Main(new GooCheat());
    }
}
