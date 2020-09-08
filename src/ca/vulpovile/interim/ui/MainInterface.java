package ca.vulpovile.interim.ui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JScrollPane;
import javax.swing.JList;

import ca.vulpovile.interim.protocolv1.ClientHandler;

public class MainInterface extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	ClientHandler ch;
	JList list = new JList();
	/**
	 * Create the frame.
	 * @param string 
	 */
	public MainInterface(ClientHandler ch, String string) {
		setTitle(string);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.ch = ch;
		setBounds(100, 100, 396, 551);
		setLocationRelativeTo(ch.li);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(new TitledBorder("Chatrooms"));
		contentPane.add(scrollPane, BorderLayout.CENTER);
		

		list.setOpaque(false);
		scrollPane.setViewportView(list);
		list.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2 && list.getSelectedIndex() >= 0)
				{
					MainInterface.this.ch.joinGroup(list.getSelectedIndex());
				}
			}
			
		});
	}

	public void updateGroups() {
		if(ch.groups != null)
		{
			list.setListData(ch.groups);
			list.revalidate();
			list.repaint();
		}
	}

}
