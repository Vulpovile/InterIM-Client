package ca.vulpovile.interim.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;

import ca.vulpovile.interim.compression.HuffmanCompressor;
import ca.vulpovile.interim.protocolv1.ClientHandler;
import ca.vulpovile.interim.protocolv1.packets.Packet2RegisterField;

import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JSplitPane;

public class RegisterWindow extends JDialog implements WindowListener, ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel();
	JTextPane txtpnHtml = new JTextPane();


	JButton btnOk = new JButton("OK");
	JButton btnCancel = new JButton("Cancel");
	ClientHandler handler;
	Thread timeoutThread = null;
	int timeout;
	JLabel lblTimeout = new JLabel("");
	boolean close = true;
	boolean disposed = false;
	public void dispose()
	{
		super.dispose();
		disposed = true;
	}
	ArrayList<JComponent> inputComponents = new ArrayList<JComponent>();
	ArrayList<Packet2RegisterField> packets = new ArrayList<Packet2RegisterField>();
	
	/**
	 * Create the dialog.
	 * 
	 * @param title
	 * @param html
	 * @param components
	 * @param input
	 * @param handler
	 * @throws IOException 
	 */
	public RegisterWindow(final String timeoutMessage, ArrayList<Packet2RegisterField> packets, byte[] input, ClientHandler handler) throws IOException {
		super(handler.li);
		this.packets = packets;
		this.handler = handler;
		timeout = new BigInteger(input).intValue();
		setModal(false);
		setBounds(100, 100, 450, 605);
		setLocationRelativeTo(handler.li);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));

		contentPanel.add(lblTimeout, BorderLayout.SOUTH);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation(250);
		splitPane.setResizeWeight(0.5);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPanel.add(splitPane, BorderLayout.CENTER);

		JScrollPane scrlpnHtml = new JScrollPane();
		splitPane.setLeftComponent(scrlpnHtml);

		txtpnHtml.setEditable(false);
		txtpnHtml.setBackground(SystemColor.control);
		txtpnHtml.setContentType("text/html");
		scrlpnHtml.setViewportView(txtpnHtml);

		JScrollPane scrpnInput = new JScrollPane();
		splitPane.setRightComponent(scrpnInput);
		scrpnInput.setPreferredSize(new Dimension(-1, 300));

		JPanel pnlInput = new JPanel();
		scrpnInput.setViewportView(pnlInput);
		pnlInput.setLayout(new GridLayout(0, 2));
		parseComponents(pnlInput);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);


		btnOk.setActionCommand("OK");
		buttonPane.add(btnOk);
		getRootPane().setDefaultButton(btnOk);

		btnCancel.setActionCommand("Cancel");
		buttonPane.add(btnCancel);

		addWindowListener(this);
		btnCancel.addActionListener(this);
		if (timeout > -1) {
			timeoutThread = new Thread() {
				public void run() {
					while (!disposed) {
						lblTimeout.setText(timeoutMessage + ": " + timeout);
						timeout--;
						try {
							Thread.sleep(1000L);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(!RegisterWindow.this.handler.isConnected())
						{
							timeoutThread = null;
							dispose();
							RegisterWindow.this.handler.disconnect();
						}
					}
				}
			};
			timeoutThread.start();
		}
		btnOk.addActionListener(this);
		btnCancel.addActionListener(this);
	}
	
	
	public void parseComponents(Container pnlInput) throws IOException
	{
		for(Packet2RegisterField packet : packets)
		{
			switch(packet.type)
			{
				case INTROHTML:
				{
					setTitle(new String(packet.title));
					txtpnHtml.setText("<html>" + new String(HuffmanCompressor.decompress(packet.input)) + "</html>");
					break;
				}
				case TEXT:
				{
					pnlInput.add(new JLabel(new String(packet.title)));
					JComponent comp = new JTextField(new String(packet.input));
					pnlInput.add(comp);
					inputComponents.add(comp);
					break;
				}
				case PASSWORD:
				{
					pnlInput.add(new JLabel(new String(packet.title)));
					JComponent comp = new JPasswordField();
					pnlInput.add(comp);
					inputComponents.add(comp);
					break;
				}
				case DROPDOWN:
				{
					pnlInput.add(new JLabel(new String(packet.title)));
					String[] items = new String(packet.input).split("\0");
					int selectedIndex = 0;
					for(int i = 0; i < items.length; i++)
					{
						if(items[i].startsWith("!"))
						{
							items[i] = items[i].substring(1);
							selectedIndex = i;
						}
					}
					JComboBox list = new JComboBox(items);
					list.setSelectedIndex(selectedIndex);
					pnlInput.add(list);
					inputComponents.add(list);
					break;
				}
				case RADIO:
				{
					pnlInput.add(new JLabel(new String(packet.title)));
					String[] items = new String(packet.input).split("\0");
					ButtonGroup bg = new ButtonGroup();
					JPanel container = new JPanel();
					for(int i = 0; i < items.length; i++)
					{
						JRadioButton radio = new JRadioButton(items[i]);
						bg.add(radio);
						if(items[i].startsWith("!"))
						{
							radio.setText(items[i].substring(1));
							radio.setSelected(true);
						}
						container.add(radio);
						inputComponents.add(radio);
					}
					pnlInput.add(container);
					break;
				}
				case CHECKBOX:
				{
					pnlInput.add(new JLabel(new String(packet.title)));
					String[] items = new String(packet.input).split("\0");
					JPanel container = new JPanel();
					for(int i = 0; i < items.length; i++)
					{
						JCheckBox check = new JCheckBox(items[i]);
						if(items[i].startsWith("!"))
						{
							check.setText(items[i].substring(1));
							check.setSelected(true);
						}
						container.add(check);
						inputComponents.add(check);
					}
					pnlInput.add(container);
					break;
				}
				default:
					break;
			}
		}
	}

	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowClosing(WindowEvent e) {
		if(close)
			RegisterWindow.this.handler.disconnect();
		
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnCancel)
		{
			dispose();
			RegisterWindow.this.handler.disconnect();
		}
		else if(e.getSource() == btnOk)
		{
			try {
				sendComponents();
				close = false;
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				StringWriter writer = new StringWriter();
				PrintWriter pw = new PrintWriter(writer);
				e1.printStackTrace(pw);
				pw.close();
				ErrorDialog.showError(this.handler.li, "Unexpected encoding exception", "Error", writer.toString());
				e1.printStackTrace();
			}
			dispose();
		}
	}


	private void sendComponents() throws UnsupportedEncodingException {
		for(Packet2RegisterField packet : packets)
		{
			switchcase:
			switch(packet.type)
			{
				case TEXT:
				{
					JTextField text = (JTextField) inputComponents.remove(0);
					handler.sendPacket(new Packet2RegisterField(packet.title, text.getText(), packet.type));
					break;
				}
				case PASSWORD:
				{
					JPasswordField text = (JPasswordField) inputComponents.remove(0);
					handler.sendPacket(new Packet2RegisterField(packet.title, new String(text.getPassword()).getBytes(), packet.type));
					break;
				}
				case DROPDOWN:
				{
					JComboBox text = (JComboBox) inputComponents.remove(0);
					handler.sendPacket(new Packet2RegisterField(packet.title, BigInteger.valueOf(text.getSelectedIndex()).toByteArray(), packet.type));
					break;
				}
				case RADIO:
				{

					int size = new String(packet.input).split("\0").length;
					System.out.println(size);
					ArrayList<JRadioButton> radios = new ArrayList<JRadioButton>();
					for(int i = 0; i < size; i++)
					{
						radios.add((JRadioButton)inputComponents.remove(0));
					}
					for(int i = 0; i < size; i++)
					{
						if(radios.get(i).isSelected())
						{
							handler.sendPacket(new Packet2RegisterField(packet.title, BigInteger.valueOf(i).toByteArray(), packet.type));
							break switchcase;
						}
					}

					handler.sendPacket(new Packet2RegisterField(packet.title, BigInteger.valueOf(-1).toByteArray(), packet.type));

					break;
				}
				case CHECKBOX:
				{
					System.out.println(new String(packet.input));
					int size = new String(packet.input).split("\0").length;
					System.out.println(size);
					byte[] inputs = new byte[size/8 + ((size % 8) > 0 ? 1 : 0)];
					//int currBit = 0;
					for(int i = 0; i < size; i++)
					{
						int shift = i % 8;
						int offset = i / 8;
						inputs[offset] <<= 1;
						if (shift == 0)
							inputs[offset] = 0;
						JCheckBox c = (JCheckBox)inputComponents.remove(0);
						inputs[offset] |= (c.isSelected() ? 1 : 0);
						/*
						inputs[currBit/8] <<= 1;
						if(c.isSelected())
							inputs[currBit/8] += 1;
						currBit++;*/
					}
					System.out.println(inputs.length);
					for(int i = 0; i < inputs.length; i++)
					{
						System.out.println(String.format("%8s", Integer.toBinaryString(inputs[i] & 0xFF)).replace(' ', '0'));
					}
					handler.sendPacket(new Packet2RegisterField(packet.title, inputs, packet.type));
					break;
				}
				default:
					break;
			}
		}
		handler.sendPacket(new Packet2RegisterField(new byte[]{}, new byte[]{} , Packet2RegisterField.Type.TIMEOUT));
	}

}
