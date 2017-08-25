package threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JOptionPane;

import controller.ClientController;

public class ProcessServerResponseThread extends Thread{

	@Override
	public void run() {
		BufferedReader inputStreamFromServer = ClientController.getInputStreamFromServer();
		PrintStream outputStreamToServer = ClientController.getOutputStreamFromServer();
		try {
			while (true) {
				String answer = inputStreamFromServer.readLine().trim();
				
				if (answer.startsWith("Online clients")) {
//					String s = answer.substring(answer.indexOf(':') + 1, answer.length());
//					String[] clients = s.split("|");
					answer = answer.substring(answer.indexOf(':') + 1, answer.length());
					String[] clients = answer.split(";");
					for (String cl : clients) {
						cl = cl.replaceAll(";", "").trim();
					}
					
					ClientController.refreshOnlineClients(clients);
				} else if(answer.equals("Goodbye!")) {
					ClientController.closeServerConnection();
					ClientController.closeServerConnectionRefreshGUI();
				} else if (!answer.startsWith("conn: yes") && !answer.startsWith("conn: no") ) {
					int i = JOptionPane.showConfirmDialog(null,
							"Da li zelite da prihvatite zahtev korisnika "
									+ answer.substring(answer.indexOf(':') + 1, answer.length()) + " za chat?",
									"Zahtev za povezivanjem", JOptionPane.YES_NO_OPTION);
					if (i == 0) {
						outputStreamToServer.println("conn: yes");
						String s = inputStreamFromServer.readLine();
						ClientController.showChatPanel();	// POZIVIVANJE NOVOG PANELA
						break;
					} else {
						outputStreamToServer.println("conn: no");
					}
				} else if (answer.startsWith("conn")) {
					if (answer.substring(answer.indexOf(':') + 2, answer.length()).equals("yes")) {
						ClientController.showChatPanel();	// POZIVIVANJE NOVOG PANELA
						break;
					} else if (answer.substring(answer.indexOf(':') + 2, answer.length()).equals("no")) {
						JOptionPane.showMessageDialog(null, "Zahtev za chat nije prihvacen!", 
								"Odbijanje zahteva", JOptionPane.INFORMATION_MESSAGE);
					}
				}
					
			}
				
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
