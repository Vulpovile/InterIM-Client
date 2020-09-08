package ca.vulpovile.interim.ui.message;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JTextArea;

public class TextMessagePanel extends MessagePanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public TextMessagePanel(boolean isSender, String username, String messageText) {
		super(isSender, username);
		
		JTextArea textArea = new JTextArea(messageText);
		textArea.setBorder(null);
		textArea.setEditable(false);
		textArea.setBackground(new Color(0,0,0,0));
		textArea.setOpaque(false);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		add(textArea, BorderLayout.CENTER);
	}

}
