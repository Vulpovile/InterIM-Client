package ca.vulpovile.interim.protocolv1;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import javax.swing.Icon;
import javax.swing.JFrame;

import ca.vulpovile.interim.Server;
import ca.vulpovile.interim.compression.LossyRGBCompressor;
import ca.vulpovile.interim.protocolv1.NetHandler;
import ca.vulpovile.interim.protocolv1.packets.*;
import ca.vulpovile.interim.ui.ChatWindow;
import ca.vulpovile.interim.ui.ErrorDialog;
import ca.vulpovile.interim.ui.LoginInterface;
import ca.vulpovile.interim.ui.MainInterface;
import ca.vulpovile.interim.ui.MessageDialog;
import ca.vulpovile.interim.ui.message.BitmapMessagePanel;
import ca.vulpovile.interim.ui.message.FileMessagePanel;
import ca.vulpovile.interim.ui.message.TextMessagePanel;
import ca.vulpovile.interim.ui.message.WavMessagePanel;

public class ClientHandler extends NetHandler {

	boolean identified = false;
	boolean connected = true;
	String login;
	char[] password;
	public LoginInterface li;
	public MainInterface mi = null;
	public ArrayList<ChatWindow> chats = new ArrayList<ChatWindow>();
	public String[] groups;
	public String username;
	File tempFolder = new File("./temp");

	public ChatWindow getChat(int id)
	{
		for(ChatWindow chat : chats)
			if(chat.id == id)
				return chat;
		return null;
	}
	
	public ClientHandler(Socket socket, LoginInterface li, String login, char[] password, boolean register) throws IOException {
		super(socket);
		if(!tempFolder.isDirectory())
			tempFolder.mkdir();
		this.li = li;
		this.login = login;
		this.password = password;
		
		sendPacket(new Packet1Identify(login, new String(password).getBytes(), register)); ////reeeeeeeeeeeeeeeeeeally insecure
		Arrays.fill(password, '\0');
	}

	//From http://oliviertech.com/java/generate-SHA256--SHA512-hash-from-a-String/
	@SuppressWarnings("unused")
	private String sha512(byte[] password) throws NoSuchAlgorithmException {
		  MessageDigest digest = MessageDigest.getInstance("SHA-512");
		  digest.reset();
		  digest.update(password);
		  return String.format("%0128x", new BigInteger(1, digest.digest()));
	}
	
	@Override
	public void disconnect() {
		if(connected)
		{
			if(mi != null)
				mi.dispose();
			li.setVisible(true);
			li.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			for(ChatWindow chat : chats)
				chat.dispose();
			chats.clear();
			connected = false;
			Server.logger.info("Server Disconnected");
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(li != null)
			{
				li.setComponentsEnabled(true);
			}
		}
	}
	
	@Override
	public void disconnect(String message) {
		if(connected)
		{
			this.sendPacket(new Packet0Disconnect(message));
			disconnect();
		}
	}

	@Override
	public void handlePacket(Packet1Identify id) {
		li.dispose();
		mi = new MainInterface(this, new String(id.login));
		username = new String(id.password);
		mi.setVisible(true);
	}

	@Override
	public void handlePacket(Packet2RegisterField id) {
		Server.logger.info("Getting registration " + id.type);
		try {
			li.gotRegisterField(id, this);
		} catch (IOException e) {
			e.printStackTrace();
			ErrorDialog.showError(li, "Disconnected", "A client side error has occured", e);
			disconnect();
		}
	}

	@Override
	public void run() {
		try {
			while(connected)
			{
				byte opcode = dis.readByte();
				Server.logger.info("Got packet " + opcode);
				Class<?> packetClass = Packet.packets.get(opcode);
				if(packetClass == null)
				{
					Server.logger.severe("Invalid opcode: " + opcode);
					break;
				}
				else
				{
					try {
						Packet packet = (Packet) packetClass.newInstance();
						packet.getPacket(dis);
						packet.handlePacket(this);
					} catch (InstantiationException e) {
						e.printStackTrace();
						break;
					} catch (IllegalAccessException e) {
						e.printStackTrace();
						break;
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		disconnect();
	}

	@Override
	public void handlePacket(Packet0Disconnect id) {
		System.out.println("Disconnected!");
		disconnect();
		new ErrorDialog(li, "Disconnected", "You have been disconnected by the server", new String(id.message)).setVisible(true);
	}

	public boolean isConnected() {
		// TODO Auto-generated method stub
		return connected;
	}

	@Override
	public void handlePacket(Packet3Alert id) {
		Icon icon;
		switch(id.type)
		{
			case INFO:
				icon = MessageDialog.ICON_INFO;
				break;
			case QUESTION:
				icon = MessageDialog.ICON_QUESTION;
				break;
			case WARN:
				icon = MessageDialog.ICON_WARN;
				break;
			default:
				icon = MessageDialog.ICON_ERROR;
		}
		new MessageDialog(li, "Server Message", new String(id.message), new String(id.details), icon).setVisible(true);
	}

	@Override
	public void handlePacket(Packet4JoinGroup id) {
		if(id.groupId < groups.length)
		{
			ChatWindow chat = getChat(id.groupId);
			if(chat == null)
			{
				chat = new ChatWindow(id.groupId, groups[id.groupId], this);
				chats.add(chat);
				chat.setVisible(true);
			}
		}
	}

	@Override
	public void handlePacket(Packet5LeaveGroup id) {
		ChatWindow chat = getChat(id.groupId);
		if(chat != null)
		{
			chats.remove(chat);
			chat.dispose();
			if(id.reason.length > 0)
			{
				new MessageDialog(li, "Server", "Removed from group " + groups[id.groupId], new String(id.reason), MessageDialog.ICON_WARN).setVisible(true);
			}
		}
	}

	@Override
	public void handlePacket(Packet6Message packet) {
		System.out.println("Got message");
		ChatWindow window = this.getChat(packet.groupId);
		if(window != null)
		{
			switch(packet.type)
			{
				case TEXT:
				{
					String text = new String(packet.getData());
					TextMessagePanel message = new TextMessagePanel(false, new String(packet.user), text);
					window.addMessage(message);
					break;
				}
				case WAV:
				{
					try {
						WavMessagePanel message = new WavMessagePanel(false, packet);
						window.addMessage(message);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}

				case BMP:
				{
					try {
						BitmapMessagePanel message = new BitmapMessagePanel(false, new String(packet.user), new String(packet.details), LossyRGBCompressor.decompress(packet.getData()));
						window.addMessage(message);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
				case FILE:
				{
					try {
						File tempFile = new File(tempFolder, UUID.randomUUID().toString());
						tempFile.deleteOnExit();
						BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(tempFile));
						stream.write(packet.getData());
						stream.close();
						FileMessagePanel message = new FileMessagePanel(false, new String(packet.user), new String(packet.details), tempFile);
						window.addMessage(message);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
				default:
					break;
			}
		}
	}

	@Override
	public void handlePacket(Packet7ListGroups id) {
		try {
			groups = id.getGroupList();
			if(mi != null)
				mi.updateGroups();
		} catch (IOException e) {
			disconnect();
			e.printStackTrace();
		}
	}

	@Override
	public void handlePacket(Packet8ListUsers id) {
		ChatWindow chat = getChat(id.groupId);
		if(chat != null)
		{
			try {
				String[] list = id.getGroupList();
				chat.updateGroup(list);
			} catch (IOException e) {
				e.printStackTrace();
				ErrorDialog.showError(li, "Disconnected", "A client side error has occured", e);
				disconnect();
			}
			
		}
	}

	public void joinGroup(int selectedIndex) {
		if(selectedIndex > -1 && selectedIndex < groups.length)
		{
			ChatWindow chat = getChat(selectedIndex);
			if(chat != null)
				return;
			sendPacket(new Packet4JoinGroup(selectedIndex));
		}
	}

}
