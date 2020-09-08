package ca.vulpovile.interim.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JCheckBox;

import ca.vulpovile.interim.compression.LZWCompressor;

public class ConfigDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	/**
	 * Create the dialog.
	 * @param loginInterface 
	 */
	public ConfigDialog(LoginInterface loginInterface) {
		super(loginInterface);
		setModal(true);
		setBounds(100, 100, 264, 119);
		setLocationRelativeTo(loginInterface);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		final JCheckBox chckbxUseHuffmanCoding = new JCheckBox("Use huffman coding instead of LZW");
		chckbxUseHuffmanCoding.setSelected(LZWCompressor.USE_HUFFMAN);
		contentPanel.add(chckbxUseHuffmanCoding);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e) {
				LZWCompressor.USE_HUFFMAN = chckbxUseHuffmanCoding.isSelected();
				dispose();
			}
			
		});
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e) {
				dispose();
			}
			
		});
		buttonPane.add(cancelButton);

	}

}
