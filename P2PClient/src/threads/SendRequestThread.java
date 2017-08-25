package threads;


import controller.ClientController;

public class SendRequestThread extends Thread{

	String selectedUser;
	
	public SendRequestThread(String selectedUser ) {
		this.selectedUser = selectedUser;
	}
	
	@Override
	public void run() {
		ClientController.getOutputStreamFromServer().println("conn: " + selectedUser);
	}
}
