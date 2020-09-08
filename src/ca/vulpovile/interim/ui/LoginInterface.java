package ca.vulpovile.interim.ui;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import ca.vulpovile.interim.protocolv1.ClientHandler;
import ca.vulpovile.interim.protocolv1.packets.Packet2RegisterField;

public class LoginInterface extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtLogin;
	private JPasswordField passwordField;
	private JTextField txtServer;
	private RegisterWindow rw = null;
	
	JButton btnConfig = new JButton("Settings");
	JButton btnLogin = new JButton("Log in");
	JButton btnRegister = new JButton("Register");
	public Thread connectionThread;

	public LoginInterface() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setSize(250,450);
		setTitle("InterIM 1.0");
		getContentPane().setLayout(null);
		
		new Packet2RegisterField();
		
		txtLogin = new JTextField();
		txtLogin.setBounds(10, 208, 224, 24);
		getContentPane().add(txtLogin);
		txtLogin.setColumns(10);
		
		JLabel lblLogin = new JLabel("Login");
		lblLogin.setBounds(10, 190, 224, 14);
		getContentPane().add(lblLogin);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(10, 246, 224, 14);
		getContentPane().add(lblPassword);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(10, 264, 224, 24);
		getContentPane().add(passwordField);
		
		btnRegister.setBounds(10, 392, 109, 23);
		getContentPane().add(btnRegister);
		

		JLabel lblServer = new JLabel("Server IP (or IP;PORT)");
		lblServer.setBounds(10, 302, 224, 14);
		getContentPane().add(lblServer);
		
		btnConfig.setBounds(125, 392, 109, 23);
		getContentPane().add(btnConfig);
		
		btnLogin.setBounds(10, 357, 224, 23);
		getContentPane().add(btnLogin);
		
		JLabel label = new JLabel("");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		Image img = null;
		try {
			img = ImageIO.read(LoginInterface.class.getResource("/icon.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		label.setBounds(10, 11, 224, 168);
		label.setIcon(new ImageIcon(img.getScaledInstance(img.getWidth(null) * label.getHeight()/img.getHeight(null), label.getHeight(), Image.SCALE_SMOOTH)));
		getContentPane().add(label);
		
		txtServer = new JTextField();
		txtServer.setColumns(10);
		txtServer.setBounds(10, 320, 224, 24);
		getContentPane().add(txtServer);
	
		
		btnRegister.addActionListener(this);
		btnLogin.addActionListener(this);
		btnConfig.addActionListener(this);


	}
	
	public void setComponentsEnabled(boolean isEnabled)
	{
		btnRegister.setEnabled(isEnabled);
		btnConfig.setEnabled(isEnabled);
		btnLogin.setEnabled(isEnabled);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnRegister)
		{
			setComponentsEnabled(false);
			final int port;
			final String[] creds = txtServer.getText().split(";");
			if(creds.length > 1)
			{
				try{
					port = Integer.parseInt(creds[1]);
				}
				catch(NumberFormatException ex)
				{
					JOptionPane.showMessageDialog(null, "Invalid port: " + creds[1], "Error", JOptionPane.ERROR_MESSAGE);
					setComponentsEnabled(true);
					return;
				}
			}
			else port = 11011;
			connectionThread = new Thread()
			{
				public void run()
				{
					Socket socket;
					try {
						socket = new Socket(creds[0],port);
						ClientHandler handler = new ClientHandler(socket, LoginInterface.this, LoginInterface.this.txtLogin.getText(), new char[]{}, true);
						handler.run();
					} catch (UnknownHostException e1) {
						StringWriter errors = new StringWriter();
						e1.printStackTrace(new PrintWriter(errors));
						ErrorDialog.showError(LoginInterface.this, "Failed to connect", "The host could not be found", errors.toString());
						LoginInterface.this.setComponentsEnabled(true);
						e1.printStackTrace();
					} catch (IOException e1) {
						StringWriter errors = new StringWriter();
						e1.printStackTrace(new PrintWriter(errors));
						ErrorDialog.showError(LoginInterface.this, "Failed to connect", "Could not connect to server", errors.toString());
						LoginInterface.this.setComponentsEnabled(true);
						e1.printStackTrace();
					}
				}
			};
			connectionThread.start();	
		}
		else if(e.getSource() == btnLogin)
		{
			setComponentsEnabled(false);
			final int port;
			final String[] creds = txtServer.getText().split(";");
			if(creds.length > 1)
			{
				try{
					port = Integer.parseInt(creds[1]);
				}
				catch(NumberFormatException ex)
				{
					JOptionPane.showMessageDialog(null, "Invalid port: " + creds[1], "Error", JOptionPane.ERROR_MESSAGE);
					setComponentsEnabled(true);
					return;
				}
			}
			else port = 11011;
			connectionThread = new Thread()
			{
				public void run()
				{
					Socket socket;
					try {
						socket = SSLSocketFactory.getDefault().createSocket(creds[0],port);
						ClientHandler handler = new ClientHandler(socket, LoginInterface.this, LoginInterface.this.txtLogin.getText(), LoginInterface.this.passwordField.getPassword(), false);
						handler.run();
					} catch (UnknownHostException e1) {
						StringWriter errors = new StringWriter();
						e1.printStackTrace(new PrintWriter(errors));
						ErrorDialog.showError(LoginInterface.this, "Failed to connect", "The host could not be found", errors.toString());
						LoginInterface.this.setComponentsEnabled(true);
						e1.printStackTrace();
					} catch (IOException e1) {
						StringWriter errors = new StringWriter();
						e1.printStackTrace(new PrintWriter(errors));
						ErrorDialog.showError(LoginInterface.this, "Failed to connect", "Could not connect to server", errors.toString());
						LoginInterface.this.setComponentsEnabled(true);
						e1.printStackTrace();
					}
				}
			};
			connectionThread.start();
		}
		else if(e.getSource() == btnConfig)
		{
			new ConfigDialog(this).setVisible(true);
		}
	}

	ArrayList<Packet2RegisterField> comps = new ArrayList<Packet2RegisterField>();
	public void gotRegisterField(Packet2RegisterField packet, ClientHandler handler) throws IOException {
		if(packet.type == Packet2RegisterField.Type.TIMEOUT)
		{
			String timeout = new String(packet.title);
			rw = new RegisterWindow(timeout, comps, packet.input, handler);
			rw.setVisible(true);
		}
		else
		{
			if(packet.type == Packet2RegisterField.Type.INTROHTML)
				comps.clear();
			comps.add(packet);
		}
	}
}
