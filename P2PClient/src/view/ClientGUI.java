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

	/**
	 * Create the frame.
	 */
	public ClientGUI() {
		setTitle("Voice chat");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 550, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		contentPane.add(getPanel()).setVisible(false);
		contentPane.add(getBtnPoveziSe());
		contentPane.add(getBtnZapocniChat()).setVisible(false);
		
	}
	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setBounds(25, 33, 271, 369);
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
				String[] values = new String[] {"dfkwefw", "e", "ewf", "we", "fwe", "fw", "ef", "wef"};
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
					if (!event.getValueIsAdjusting()){
			            JList source = (JList)event.getSource();
			            String selected = source.getSelectedValue().toString();
			            getBtnZapocniChat().setVisible(true);
			        }
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
						String username = ClientController.openDialogForUsername();
						ClientController.createConnection(username);
						getPanel().setVisible(true);
						getBtnPoveziSe().setVisible(false);
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
				}
			});
			btnZapocniChat.setBounds(371, 175, 120, 31);
		}
		return btnZapocniChat;
	}
}
