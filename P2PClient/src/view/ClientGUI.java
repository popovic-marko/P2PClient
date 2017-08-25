package view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import controller.ClientController;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.AbstractListModel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class ClientGUI extends JFrame {

	private JPanel contentPane;
	private JPanel panel;
	private JLabel lblListaDostupnihKlijenata;
	private JPanel panelScroll;
	private JScrollPane scrollPane;
	private JList list;
	private JButton btnPoveziSe;
	private JButton btnZapocniChat;
	private JButton btnPrekiniVezu;
	private JPanel chatPanel;
	private JButton btnPrekiniChat;
	private JButton btnOsvezi;

	/**
	 * Create the frame.
	 */
	public ClientGUI() {
		setTitle("Voice chat");
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// prekinuti konekciju ako je aktivna, nije zavrseno
				ClientController.getOutputStreamFromServer().println("end");
				ClientController.closeServerConnection();
				
				System.exit(0);
			}
		});
		setBounds(100, 100, 550, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.add(getPanel()).setVisible(false);
		contentPane.add(getBtnPoveziSe());
		contentPane.add(getBtnZapocniChat()).setVisible(false);
		contentPane.add(getBtnPrekiniVezu()).setVisible(false);
		contentPane.add(getChatPanel()).setVisible(false);
		contentPane.add(getBtnOsvezi()).setVisible(false);
	}
	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setBounds(25, 33, 271, 260);
			panel.setLayout(null);
			panel.add(getLblListaDostupnihKlijenata());
			panel.add(getPanelScroll());
		}
		return panel;
	}
	private JLabel getLblListaDostupnihKlijenata() {
		if (lblListaDostupnihKlijenata == null) {
			lblListaDostupnihKlijenata = new JLabel("Lista dostupnih klijenata");
			lblListaDostupnihKlijenata.setBounds(25, 11, 151, 32);
		}
		return lblListaDostupnihKlijenata;
	}
	private JPanel getPanelScroll() {
		if (panelScroll == null) {
			panelScroll = new JPanel();
			panelScroll.setBounds(25, 48, 186, 222);
			panelScroll.setLayout(null);
			panelScroll.add(getScrollPane());
		}
		return panelScroll;
	}
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setBounds(0, 11, 186, 200);
			scrollPane.setViewportView(getList_1());
		}
		return scrollPane;
	}
	private JList getList_1() {
		if (list == null) {
			list = new JList();
			list.setModel(new AbstractListModel() {
				String[] values = new String[] {""};
				public int getSize() {
					return values.length;
				}
				public Object getElementAt(int index) {
					return values[index];
				}
			});
			list.addListSelectionListener(new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent event) {
					if (!event.getValueIsAdjusting() && ClientController.indicator == false){
			            JList source = (JList)event.getSource();
			            String selected = source.getSelectedValue().toString();
			            if(!selected.equals(""))
			            	getBtnZapocniChat().setVisible(true);
			        }
					if(ClientController.indicator == true)
						ClientController.indicator = false;
				}
			});
		}
		return list;
	}
	private JButton getBtnPoveziSe() {
		if (btnPoveziSe == null) {
			btnPoveziSe = new JButton("Povezi se na server");
			btnPoveziSe.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
	//					String username = ClientController.openDialogForUsername();
					//	ClientController.createConnection();	
						ClientController.createConnection();				// SAMO POD KOM. ZBOG TESTA
						ClientController.sendUsernameAndGetOnlineClients();
						getPanel().setVisible(true);
						getBtnPoveziSe().setVisible(false);
						getBtnPrekiniVezu().setVisible(true);
						getBtnOsvezi().setVisible(true);
						ClientController.startServerResponseThread();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage(), 
								"Greska", JOptionPane.ERROR_MESSAGE);
					} 			
				}
			});
			
			btnPoveziSe.setBounds(203, 413, 148, 31);
		}
		return btnPoveziSe;
	}
	private JButton getBtnZapocniChat() {
		if (btnZapocniChat == null) {
			btnZapocniChat = new JButton("Zapocni chat");
			btnZapocniChat.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String selectedUser = list.getSelectedValue().toString();
					ClientController.sendRequest(selectedUser);
				}
			});
			btnZapocniChat.setBounds(371, 175, 120, 31);
		}
		return btnZapocniChat;
	}
	private JButton getBtnPrekiniVezu() {
		if (btnPrekiniVezu == null) {
			btnPrekiniVezu = new JButton("Prekini vezu sa serverom");
			btnPrekiniVezu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
//					try {
						ClientController.getOutputStreamFromServer().println("end");
//						if(ClientController.getInputStreamFromServer().readLine()
//								.trim().equals("Goodbye!")) {
//							
//							ClientController.getOutputStreamFromServer().close();
//							ClientController.getInputStreamFromServer().close();
//							ClientController.getSocketForComunnication().close();
//							
//							panel.setVisible(false);
//							btnPrekiniVezu.setVisible(false);
//							btnZapocniChat.setVisible(false);
//							btnOsvezi.setVisible(false);
//							btnPoveziSe.setVisible(true);
//						} else {
//							throw new Exception("Greska prilikom prekidanja veze!");
//						}
//						
//					} catch (Exception e1) {
//						//System.out.println(e1.getMessage());
//						JOptionPane.showMessageDialog(null, e1.getMessage(), 
//								"Greska", JOptionPane.ERROR_MESSAGE);
//					}
				}
			});
			btnPrekiniVezu.setBounds(192, 420, 180, 31);
		}
		return btnPrekiniVezu;
	}
	
	private JPanel getChatPanel() {
		if (chatPanel == null) {
			chatPanel = new JPanel();
			chatPanel.setBounds(88, 122, 340, 187);
			chatPanel.setLayout(null);
			chatPanel.add(getPrekiniChat());
		}
		return chatPanel;
	}
	
	private JButton getPrekiniChat() {
		if (btnPrekiniChat == null) {
			btnPrekiniChat = new JButton("Prekini chat");
			btnPrekiniChat.setBounds(126, 146, 109, 30);
		}
		return btnPrekiniChat;
	}

	private JButton getBtnOsvezi() {
		if (btnOsvezi == null) {
			btnOsvezi = new JButton("Osvezi");
			btnOsvezi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ClientController.sendRequestForRefresh();
					ClientController.indicator = true;
					// trebalo bi da se vrati lista koja ce biti postavljena na model
//					try {
//						ClientController.getClientsAndRefresh();
//					} catch (IOException e1) {
//						System.out.println(e1.getMessage());
//					}
				}
			});
			btnOsvezi.setBounds(80, 310, 89, 27);
		}
		return btnOsvezi;
	}
	
	public void showChatPanel() {
		panel.setVisible(false);
		btnZapocniChat.setVisible(false);
		btnPrekiniVezu.setVisible(false);
		chatPanel.setVisible(true);
	}
	public void refreshOnlineClients(String[] clients) {
		// postaviti vrednosti u listu
		if (!clients[0].trim().equals("null")) {
			getList_1().setModel(new AbstractListModel() {
				// String[] values = (clients == null ? new String[] {""} : clients);
				String[] values = clients;

				public int getSize() {
					return values.length;
				}

				public Object getElementAt(int index) {
					return values[index];
				}
			});
		}
	}

	public void closeServerConnection() {
		panel.setVisible(false);
		btnPrekiniVezu.setVisible(false);
		btnZapocniChat.setVisible(false);
		btnOsvezi.setVisible(false);
		btnPoveziSe.setVisible(true);
	}

}
