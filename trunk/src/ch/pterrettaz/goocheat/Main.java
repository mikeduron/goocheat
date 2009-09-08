package ch.pterrettaz.goocheat;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Main extends JFrame{
	private static final long serialVersionUID = 1L;
	protected final GooCheat gooCheat;
	private final JTextField text;
	private final JButton button;
	private final JTextArea result;
	
	public Main(final GooCheat gooCheat) {
		this.gooCheat = gooCheat;
		String version = "";
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("version.txt")));
			version = in.readLine();
			in.close();
		} catch (Exception e1) { }
		setTitle("Goo Cheat - " + version);
		setSize(300, 200);
		Dimension windowDim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(windowDim.width/2 - getWidth()/2, windowDim.height/2 - getHeight()/2);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);
		c.weightx = 1;
		c.weighty = 0;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		text = new JTextField();
		text.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
				JTextField source = (JTextField) e.getSource();
				if (source.getText().length() > 19)
					source.setText(source.getText().substring(0, 19));	
			}
			public void keyReleased(KeyEvent e) {
				
			}
			public void keyPressed(KeyEvent e) {
				JTextField source = (JTextField) e.getSource();
				if (source.getText().length() < 15) {
					button.setEnabled(false);
					performSearch();
				} else {
					button.setEnabled(true);
				}
			}
		});
		
		panel.add(text, c);
		
		c.weightx = 0;
		c.gridx = 1;
		c.fill = GridBagConstraints.NONE;
		button = new JButton("generate");
		button.setEnabled(false);
		panel.add(button, c);
		
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		result = new JTextArea();
		panel.add(result, c);
		setContentPane(panel);
		setVisible(true);
		getRootPane().setDefaultButton(button);
		
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				button.setEnabled(false);
				performSearch();
				button.setEnabled(true);
			}
		});
	}
	
	private void performSearch() {
		try {
			result.setText("");
			if (text.getText().equals(""))
				return;
			
			final List<String> ret = new LinkedList<String>();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					ret.addAll(gooCheat.getValidWordsPermutation(text.getText()));
					for (String word: ret) {
						result.append(word + '\n');
					}
				}
			});
		} catch (Exception ex) { }
	}
	
	
	public static void main(String[] args) throws Exception {
		new Main(new GooCheat(10));
	}
}
