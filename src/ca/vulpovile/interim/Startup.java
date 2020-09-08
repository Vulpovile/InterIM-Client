package ca.vulpovile.interim;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Provider;
import java.security.Provider.Service;
import java.security.Security;
import java.util.Properties;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import ca.vulpovile.interim.ui.LoginInterface;

public class Startup {
	static Properties prop = new Properties();
	static File propFile = new File("client.cfg");
	public static void main(String[] args)
	{
		for (Provider provider: Security.getProviders()) {
			for(Service s : provider.getServices())
			{
				if(s.getType().equalsIgnoreCase("Cipher"))
				{
					System.out.println(s.getAlgorithm());
				}
			}
		}
		loadProperties();
		setUI();
		LoginInterface login = new LoginInterface();
		login.setLocationRelativeTo(null);
		login.setVisible(true);
		storeProperties();
	}

	public static void loadProperties(){
		try{
		BufferedInputStream stream = new BufferedInputStream(new FileInputStream(propFile));
		prop.load(stream);
		stream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void storeProperties(){
		try{
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(propFile));
			prop.store(out, "This is a test\r\nIt should have many lines\r\n");
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void setUI() {
		if(prop.getProperty("laf", "").trim().length() != 0)
		{
			try {
				UIManager.setLookAndFeel(prop.getProperty("laf"));
				System.out.println("Found LAF!");
				return;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedLookAndFeelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
		boolean cont = true;
		for(LookAndFeelInfo laf : lafs)
		{
			if(laf.getName().toLowerCase().contains("nimbus"))
				try {
					UIManager.setLookAndFeel(laf.getClassName());
					prop.setProperty("laf", laf.getClassName());
					return;
				} catch (Exception e) {}
			else if(laf.getName().toLowerCase().contains("gtk"))
			{
				try {
					UIManager.setLookAndFeel(laf.getClassName());
					prop.setProperty("laf", laf.getClassName());
					return;
				} catch (Exception e) {}
			}
		}		
		if(cont)
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				prop.setProperty("laf", UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
