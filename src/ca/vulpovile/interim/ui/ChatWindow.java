package ca.vulpovile.interim.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.UIManager;

import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JTextArea;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import ca.vulpovile.interim.compression.LossyRGBCompressor;
import ca.vulpovile.interim.fileformat.BitmapFormat;
import ca.vulpovile.interim.fileformat.WavData;
import ca.vulpovile.interim.fileformat.WavFormat;
import ca.vulpovile.interim.protocolv1.ClientHandler;
import ca.vulpovile.interim.protocolv1.packets.Packet6Message;
import ca.vulpovile.interim.protocolv1.packets.Packet6Message.MessageType;
import ca.vulpovile.interim.ui.message.BitmapMessagePanel;
import ca.vulpovile.interim.ui.message.MessagePanel;
import ca.vulpovile.interim.ui.message.TextMessagePanel;
import ca.vulpovile.interim.ui.message.WavMessagePanel;

public class ChatWindow extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	public final int id;
	JList lstUsers = new JList();
	JPanel pnlMessage = new JPanel();
	File selectedFile = new File("./");

	JButton btnSend = new JButton("Send");
	JButton btnFile = new JButton(UIManager.getIcon("FileView.fileIcon"));
	JTextArea txtMessage = new JTextArea();
	ClientHandler handler;
	/**
	 * Create the frame.
	 */
	public ChatWindow(int id, String name, ClientHandler client) {
		handler = client;
		this.id = id;
		setTitle(name);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 700, 543);
		setLocationRelativeTo(client.mi);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JSplitPane spltPnUsers = new JSplitPane();
		spltPnUsers.setResizeWeight(1.0);
		contentPane.add(spltPnUsers, BorderLayout.CENTER);
		
		JPanel pnlChat = new JPanel();
		spltPnUsers.setLeftComponent(pnlChat);
		pnlChat.setLayout(new BorderLayout(0, 0));
		
		JSplitPane spltPnChat = new JSplitPane();
		spltPnChat.setResizeWeight(1.0);
		spltPnChat.setOrientation(JSplitPane.VERTICAL_SPLIT);
		pnlChat.add(spltPnChat, BorderLayout.CENTER);
		
		JPanel pnlInput = new JPanel();
		spltPnChat.setRightComponent(pnlInput);
		pnlInput.setLayout(new BorderLayout(5, 5));
		
		pnlInput.add(btnFile, BorderLayout.WEST);
		
		pnlInput.add(btnSend, BorderLayout.EAST);
		
		JScrollPane scrlPnInput = new JScrollPane();
		scrlPnInput.setPreferredSize(new Dimension(-1, 50));
		pnlInput.add(scrlPnInput, BorderLayout.CENTER);
		
		scrlPnInput.setViewportView(txtMessage);
		
		JPanel pnlOutput = new JPanel();
		spltPnChat.setLeftComponent(pnlOutput);
		pnlOutput.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel = new JLabel("Chat");
		pnlOutput.add(lblNewLabel, BorderLayout.NORTH);
		
		JScrollPane scrlpnMessages = new JScrollPane();
		pnlOutput.add(scrlpnMessages, BorderLayout.CENTER);
		

		pnlMessage.setBackground(SystemColor.text);
		scrlpnMessages.setViewportView(pnlMessage);
		pnlMessage.setLayout(new BoxLayout(pnlMessage, BoxLayout.Y_AXIS));
		
		JPanel pnlUsers = new JPanel();
		spltPnUsers.setRightComponent(pnlUsers);
		pnlUsers.setLayout(new BorderLayout(0, 0));
		
		JLabel lblConnectedUsers = new JLabel("Connected Users");
		pnlUsers.add(lblConnectedUsers, BorderLayout.NORTH);
		
		JScrollPane scrlpnUsers = new JScrollPane();
		pnlUsers.add(scrlpnUsers, BorderLayout.CENTER);
		

		lstUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrlpnUsers.setViewportView(lstUsers);
		btnSend.addActionListener(this);
		btnFile.addActionListener(this);
	}
	public void updateGroup(String[] list) {
		lstUsers.setListData(list);
		lstUsers.revalidate();
		lstUsers.repaint();
	}
	public void addMessage(MessagePanel message)
	{
		pnlMessage.add(message);
		if(pnlMessage.getComponents().length > 50)
			pnlMessage.remove(0);
		pnlMessage.revalidate();
		pnlMessage.repaint();
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnSend)
		{
			Packet6Message message = new Packet6Message(id, txtMessage.getText().trim(), "");
			if(txtMessage.getText().trim().length() > 0)
			{
				handler.sendPacket(message);
				addMessage(new TextMessagePanel(true, handler.username, txtMessage.getText().trim()));
				txtMessage.setText(null);
			}
		}
		if(e.getSource() == btnFile)
		{
			JFileChooser chooser = new JFileChooser();
			chooser.setSelectedFile(selectedFile);
			if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
			{
				selectedFile = chooser.getSelectedFile();
				if(selectedFile.getName().toLowerCase().endsWith(".bmp"))
				{
					short[][][] rgb = BitmapFormat.readBMP(selectedFile);
					if(rgb != null)
					{
						byte[] compressed = LossyRGBCompressor.compress(rgb, (byte)2);
						if(compressed != null)
						{
							rgb = null;
							handler.sendPacket(new Packet6Message(id, compressed, selectedFile.getName().getBytes(), "", MessageType.BMP));
							try {
								addMessage(new BitmapMessagePanel(true, handler.username, selectedFile.getName(), LossyRGBCompressor.decompress(compressed)));
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							return;
						}
					}
				}
				else if(selectedFile.getName().toLowerCase().endsWith(".wav"))
				{
					WavData waveData = WavFormat.readWAV(selectedFile);
					if(waveData != null)
					{
						try {
							Packet6Message message = new Packet6Message(id, waveData, handler.username);
							handler.sendPacket(message);
							addMessage(new WavMessagePanel(true, message));
							return;
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
				try {
					BufferedInputStream stream = new BufferedInputStream(new FileInputStream(selectedFile));
					byte[] fileBytes = new byte[(int) selectedFile.length()];
					stream.read(fileBytes);
					stream.close();
					Packet6Message message = new Packet6Message(id, fileBytes, selectedFile.getName().getBytes(), "", Packet6Message.MessageType.FILE);
					handler.sendPacket(message);
					addMessage(new TextMessagePanel(true, handler.username, "Sent file " + selectedFile.getName()));
				} catch (IOException e1) {
					ErrorDialog.showError(this, "Could not load file", "The selected file could not be read", e1);
					e1.printStackTrace();
				}
			}
		}
	}
}
