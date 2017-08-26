package controller;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import javax.management.RuntimeErrorException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JOptionPane;

import threads.SendRequestThread;
import threads.PeerResponseThread;
import threads.ProcessServerResponseThread;
import view.ClientGUI;

public class ClientController {

	// oznaca da li je kliknuto na 'Osvezi' kako se ne bi pozivao valueChanged
	public static boolean indicator = false;
	
	private static ClientGUI clientGUI;
	private static Socket socketForComunnication;
	private static PrintStream outputStreamToServer;
	private static BufferedReader inputStreamFromServer;
	
	private static String peerIp;
	private static int peerPort;
	
	public static int myUDPPort;
	public static AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
//	private static AudioInputStream audioInputStream;
    private static TargetDataLine microphone;
    private static SourceDataLine speakers;
    
	private static DatagramSocket datagramSocket;
    
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
				myUDPPort = findAvailablePort();				
				outputStreamToServer.println(myUDPPort);
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
	
	public static void beginChat() {
		try {
			datagramSocket = new DatagramSocket(myUDPPort);
			
			PeerResponseThread peerResponse = new PeerResponseThread();
			peerResponse.start();
			
			microphone = AudioSystem.getTargetDataLine(format);

            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);

            int numBytesRead;
            int bytesRead = 0;
            
            microphone.start();
            
			while (bytesRead < 100000) { 
				byte[] dataForPeer = new byte[1024];
				numBytesRead = microphone.read(dataForPeer, 0, 1024);
				bytesRead = bytesRead + numBytesRead;

//				System.out.println(bytesRead);
//				out.write(data, 0, numBytesRead);
																	//					//
				DatagramPacket datagramPacket = new DatagramPacket(dataForPeer, dataForPeer.length, 
						InetAddress.getByName(peerIp), peerPort);
				sendOrRecievePacket(true, datagramPacket);
			}
			microphone.close();
          
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static synchronized DatagramPacket sendOrRecievePacket(boolean b, DatagramPacket packet) {
		try {
			if (b && packet != null) {
				datagramSocket.send(packet);

				return null;
			} else {
				datagramSocket.receive(packet);

				return packet;
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	public static int findAvailablePort() {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(0);
			socket.setReuseAddress(true);
			int port = socket.getLocalPort();
			try {
				socket.close();
			} catch (IOException e) { }
			
			return port;
		} catch (IOException e) { } 
		finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) { }
			}
		}
		throw new RuntimeException("Nije moguce pronaci port!");
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

	public static String getPeerIp() {
		return peerIp;
	}

	public static void setPeerIp(String peerIp) {
		ClientController.peerIp = peerIp;
	}

	public static int getPeerPort() {
		return peerPort;
	}

	public static void setPeerPort(int peerPort) {
		ClientController.peerPort = peerPort;
	}
}
