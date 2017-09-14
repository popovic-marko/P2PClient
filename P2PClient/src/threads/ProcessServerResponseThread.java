package threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JOptionPane;

import controller.ClientController;

public class ProcessServerResponseThread extends Thread {

	@Override
	public void run() {
		BufferedReader inputStreamFromServer = ClientController.getInputStreamFromServer();
		PrintStream outputStreamToServer = ClientController.getOutputStreamFromServer();
		try {
			while (true) {
				String answer = inputStreamFromServer.readLine().trim();

				if (answer.startsWith("Online clients")) {
					answer = answer.substring(answer.indexOf(':') + 1, answer.length());
					String[] clients = answer.split(";");
					for (String cl : clients) {
						cl = cl.replaceAll(";", "").trim();
					}

					ClientController.refreshOnlineClients(clients);
				} else if (answer.equals("Goodbye!")) {
					ClientController.closeServerConnection();
					ClientController.closeServerConnectionRefreshGUI();
				} else if (answer.endsWith("Accept?")) {
					int i = JOptionPane.showConfirmDialog(null, answer.trim(), "Zahtev za povezivanjem",
							JOptionPane.YES_NO_OPTION);
					if (i == 0) {
						outputStreamToServer.println("yes");
						String ipAndPort = inputStreamFromServer.readLine().trim();

						String ip = ipAndPort.substring(ipAndPort.indexOf("/") + 1, ipAndPort.lastIndexOf("/"));
						int port = Integer.parseInt(ipAndPort.substring(ipAndPort.lastIndexOf("/") + 1));
						ClientController.setPeerIp(ip);
						ClientController.setPeerPort(port);

						ClientController.showChatPanel();
						ClientController.beginChat();
						break;
					} else {
						outputStreamToServer.println("no");
					}
				} else if (answer.startsWith("conn")) {
					if (answer.startsWith("conn: yes")) {
						String ip = answer.substring(answer.indexOf("/") + 2, answer.lastIndexOf("/"));
						int port = Integer.parseInt(answer.substring(answer.lastIndexOf("/") + 1, answer.length()));
						ClientController.setPeerIp(ip);
						ClientController.setPeerPort(port);

						ClientController.showChatPanel();
						ClientController.beginChat();
						break;
					} else if (answer.equals("conn: no")) {
						JOptionPane.showMessageDialog(null, "Zahtev za chat nije prihvacen!", "Odbijanje zahteva",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}

			}

		} catch (IOException e) {
		}
	}

}
