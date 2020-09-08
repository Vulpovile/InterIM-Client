package ca.vulpovile.interim.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MessageDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	JTextArea textArea;
	public static final Icon ICON_ERROR = UIManager.getIcon("OptionPane.errorIcon");
	public static final Icon ICON_INFO = UIManager.getIcon("OptionPane.informationIcon");
	public static final Icon ICON_QUESTION = UIManager.getIcon("OptionPane.questionIcon");
	public static final Icon ICON_WARN = UIManager.getIcon("OptionPane.warningIcon");

	
	/**
	 * Create the dialog.
	 * @param window 
	 */
	public MessageDialog(JFrame window, String title, String message, String detail, Icon icon) {
		super(window, title);
		setBounds(100, 100, 536, 300);
		setLocationRelativeTo(window);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 10, 10, 10));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		contentPanel.add(scrollPane, BorderLayout.CENTER);
		
		JTextArea textArea = new JTextArea(detail);
		textArea.setBackground(SystemColor.control);
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setHgap(0);
		flowLayout.setAlignment(FlowLayout.LEFT);
		contentPanel.add(panel, BorderLayout.NORTH);
		
		JLabel label = new JLabel(message);
		label.setIcon(icon);
		panel.add(label);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				dispose();
			}
			
		});
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

	}

}
