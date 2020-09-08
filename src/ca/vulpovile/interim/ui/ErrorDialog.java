package ca.vulpovile.interim.ui;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorDialog extends MessageDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ErrorDialog(JFrame window, String title, String message, String detail) {
		super(window, title, message, detail, MessageDialog.ICON_ERROR);
		setModal(true);
		// TODO Auto-generated constructor stub
	}
	JTextArea textArea;
	
	public static void showError(JFrame frame, String title, String error, Throwable ex) {
		StringWriter errors = new StringWriter();
		ex.printStackTrace(new PrintWriter(errors));
		new ErrorDialog(frame, title, error, ex.toString()).setVisible(true);
	}
	public static void showError(JFrame frame, String title, String error, String details) {
		new ErrorDialog(frame, title, error, details).setVisible(true);
	}

}
