package ca.vulpovile.interim.ui.message;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class BitmapMessagePanel extends MessagePanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JLabel imageLabel = new JLabel();
	/**
	 * Create the panel.
	 * @throws IOException 
	 */
	public BitmapMessagePanel(boolean isSender, String username, String fileName, short[][][] rgb) throws IOException {
		super(isSender, username);
		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout());
		panel.setOpaque(false);
		JLabel lblFilename = new JLabel(new String(fileName));
		lblFilename.setOpaque(false);
		panel.add(lblFilename, BorderLayout.NORTH);
		imageLabel.setVerticalAlignment(SwingConstants.TOP);
		imageLabel.setIcon(new ImageIcon(makeBufferedImage(rgb)));
		panel.add(imageLabel, BorderLayout.CENTER);
	}

	private BufferedImage makeBufferedImage(short[][][] rgbbytes) {
		//Create a displayable image of the size of the array
		BufferedImage img = new BufferedImage(rgbbytes[0].length, rgbbytes.length, BufferedImage.TYPE_3BYTE_BGR);
		for(int i = 0; i < rgbbytes.length; i++)
		{
			for(int j = 0; j < rgbbytes[i].length; j++)
			{
				//Full alpha, tack on the R, G, and B by setting them to 0x00**0000, 0x0000**00, and 0x000000** and ORing them
				//Make a 32 bit color integer to plot onto the buffer
				int rgb = 0xFF000000 | (rgbbytes[i][j][0] << 16) | (rgbbytes[i][j][1] << 8) | (rgbbytes[i][j][2] << 0);
				//Plot a pixel on the buffered image
				img.setRGB(j, rgbbytes.length - i - 1, rgb);
			}
		}
		//Return the image
		return img;
	}

}
