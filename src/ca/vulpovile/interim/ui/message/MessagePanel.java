package ca.vulpovile.interim.ui.message;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

public abstract class MessagePanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public MessagePanel(boolean isSender, String name) {
		setLayout(new BorderLayout(0, 0));
		setOpaque(false);
		JLabel lblUsername = new JLabel(name+":");
		lblUsername.setFont(new Font(lblUsername.getFont().getName(), Font.BOLD, lblUsername.getFont().getSize()));
		if(isSender)
			lblUsername.setForeground(Color.GREEN.darker());
		lblUsername.setOpaque(false);
		add(lblUsername, BorderLayout.NORTH);

	}

}
