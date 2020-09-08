package ca.vulpovile.interim.ui.message;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

public class FileMessagePanel extends MessagePanel implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JButton btnSave = new JButton("Store");
	JButton btnDelete = new JButton("Delete");
	File tempFile;
	String fileName;
	/**
	 * Create the panel.
	 * @throws IOException 
	 */
	public FileMessagePanel(boolean isSender, String userName, String fileName, File tempFile) throws IOException {
		super(isSender, userName);
		this.tempFile = tempFile;
		this.fileName = fileName;
		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		panel.setOpaque(false);
		panel.add(btnSave);
		
		panel.add(btnDelete);
		
		JLabel lblFilename = new JLabel(fileName);
		lblFilename.setOpaque(false);
		panel.add(lblFilename);
		
		btnSave.addActionListener(this);
		btnDelete.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnSave)
		{
			JFileChooser chooser = new JFileChooser();
			chooser.setSelectedFile(new File("./", fileName));
			if(chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
			{
				tempFile.renameTo(chooser.getSelectedFile());
			}
		}
		else if(e.getSource() == btnDelete)
		{
			tempFile.delete();
			btnSave.setEnabled(false);
			btnDelete.setEnabled(false);
		}
	}

}
