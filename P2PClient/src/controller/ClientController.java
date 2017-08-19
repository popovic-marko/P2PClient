package controller;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Properties;

import javax.swing.JOptionPane;

import view.ClientGUI;

public class ClientController {
	
	private static ClientGUI clientGUI;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					clientGUI = new ClientGUI();
					clientGUI.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void createConnection(String username) throws Exception {
		String[] data = loadIpAndPort();
		Socket socketForComunnication = new Socket(data[0], Integer.parseInt(data[1]));
		
		DataOutputStream outputStreamToServer = new DataOutputStream(socketForComunnication.getOutputStream());
		BufferedReader inputStreamFromServer = new BufferedReader(
												new InputStreamReader(socketForComunnication.getInputStream()));
		
		outputStreamToServer.writeBytes("User:" + username);
		String answer = inputStreamFromServer.readLine();
		if (answer != "success")
			throw new Exception(answer);
	}
	
	public static String openDialogForUsername() {
		String username = JOptionPane.showInputDialog(null, "Unesite username: ", "Unos", JOptionPane.DEFAULT_OPTION);
		username = (username != null) ? username : "user1"; 

		return username;
	}
	
	public static String[] loadIpAndPort() throws Exception {
		Properties prop = new Properties();
		InputStream input = new FileInputStream("data.properties");
		
		prop.load(input);
		
		return new String[] {prop.getProperty("ip"), prop.getProperty("port")};
	}
}
