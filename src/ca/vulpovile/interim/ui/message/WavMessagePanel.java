package ca.vulpovile.interim.ui.message;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JPanel;

import ca.vulpovile.interim.fileformat.WavData;
import ca.vulpovile.interim.fileformat.WavInputStream;
import ca.vulpovile.interim.protocolv1.packets.Packet6Message;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;

public class WavMessagePanel extends MessagePanel implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JButton btnPlay = new JButton("Play");
	JButton btnStop = new JButton("Stop");
	WavData data;
	/**
	 * Create the panel.
	 * @throws IOException 
	 */
	public WavMessagePanel(boolean isSender, Packet6Message message) throws IOException {
		super(isSender, new String(message.user));
		data = message.getWavData();
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		panel.add(btnPlay);
		panel.add(btnStop);
		
		JLabel lblFilename = new JLabel(new String(data.fileName));
		panel.add(lblFilename);
		
		btnPlay.addActionListener(this);
		btnStop.addActionListener(this);
	}
	Clip clip = null;
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnPlay)
		{
			try {
				if(clip == null)
				{
					clip = AudioSystem.getClip();
					AudioInputStream inputStream = WavInputStream.getWavInputStream(data);
					clip.open(inputStream);
					clip.addLineListener(new LineListener()
					{

						public void update(LineEvent event) {
							if(event.getType() == LineEvent.Type.STOP)
							{
								clip.close();
								clip = null;
							}
						}
						
					});
				}
				clip.start();
			} catch (LineUnavailableException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
		}
		else if(e.getSource() == btnStop && clip != null)
			clip.stop();
	}

}
