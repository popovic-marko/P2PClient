package controller;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Properties;

import javax.swing.JOptionPane;

import threads.SendRequestThread;
import threads.ProcessServerResponseThread;
import view.ClientGUI;

public class ClientController {

	// oznaca da li je kliknuto na 'Osvezi' kako se ne bi pozivao valueChanged
	public static boolean indicator = false;
	
	private static ClientGUI clientGUI;
	private static Socket socketForComunnication;
	private static PrintStream outputStreamToServer;
	private static BufferedReader inputStreamFromServer;
	
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
	
	public static void createConnection() throws Exception {
		String[] data = loadIpAndPort();
		socketForComunnication = new Socket(data[0], Integer.parseInt(data[1]));
		
		outputStreamToServer = new PrintStream(socketForComunnication.getOutputStream());
		inputStreamFromServer = new BufferedReader(
												new InputStreamReader(socketForComunnication.getInputStream()));
//		if(inputStreamFromServer.readLine().equals("Welcome!")) {
//			inputStreamFromServer.readLine();
//			outputStreamToServer.writeBytes(username);
//			String answer = inputStreamFromServer.readLine().trim();
//			if (!answer.startsWith("Welcome"))
//				throw new Exception(answer);
//		}
	}
	
	public static void sendUsernameAndGetOnlineClients() {
		try {
//			if(inputStreamFromServer.readLine().equals("Welcome!")) {
//				inputStreamFromServer.readLine();
//				outputStreamToServer.writeBytes(userName);
//				String answer = inputStreamFromServer.readLine().trim();
//				if (!answer.startsWith("Welcome")) {
//					boolean end = false;
//					while (!end) {
//						inputStreamFromServer.readLine();
//						String username = ClientController.openDialogForUsername();
//						outputStreamToServer.writeBytes(username);
//						if (answer.startsWith("Welcome"))
//							end = true;
//					}
//				}
//			}
			if (inputStreamFromServer.readLine().equals("Welcome!")) {
				boolean end = false;
				while (!end) {
					inputStreamFromServer.readLine();
					String username = ClientController.openDialogForUsername();
					outputStreamToServer.println(username);
					if (inputStreamFromServer.readLine().trim().startsWith("Welcome"))
						end = true;
				}
				
				getClientsAndRefresh();
			}// ako ne udje u IF nista se ne radi
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void getClientsAndRefresh() throws IOException {
		String clientList = inputStreamFromServer.readLine().trim();
		clientList = clientList.substring(clientList.indexOf(':') + 1, clientList.length());
		String[] clients = clientList.split(";");
		for (String cl : clients) {
			cl = cl.replaceAll(";", "").trim();
		}
		clientGUI.refreshOnlineClients(clients);
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
	
	public static void startServerResponseThread() {
		ProcessServerResponseThread responseThread = new ProcessServerResponseThread();
		responseThread.start();
		//POGLEDATI OVO OVDE
		
	}

	public static void sendRequest(String selectedUser) {
		SendRequestThread sendRequestThread = new SendRequestThread(selectedUser);
		sendRequestThread.start();
	}
	
	public static void showChatPanel() {
		clientGUI.showChatPanel();
	}

	public static void sendRequestForRefresh() {
		try {
			outputStreamToServer.println("getOnline");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void refreshOnlineClients(String[] clients) {
		clientGUI.refreshOnlineClients(clients);
	}

	public static void closeServerConnection() {
		try {
			ClientController.getOutputStreamFromServer().close();
			ClientController.getInputStreamFromServer().close();
			ClientController.getSocketForComunnication().close();
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	public static void closeServerConnectionRefreshGUI() {
		clientGUI.closeServerConnection();
	}
	
	public static BufferedReader getInputStreamFromServer() {
		return inputStreamFromServer;
	}
	public static PrintStream getOutputStreamFromServer() {
		return outputStreamToServer;
	}

	public static Socket getSocketForComunnication() {
		return socketForComunnication;
	}

}
