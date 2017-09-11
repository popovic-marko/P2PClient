package threads;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import controller.ClientController;

public class PeerResponseThread extends Thread{
	
	@Override
	public void run() {
		AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
		AudioInputStream audioInputStream;
		SourceDataLine speakers = null;

		try {
			DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
			speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			speakers.open(format);
			speakers.start();
			
			while (true) {
				byte[] data = new byte[1024];
				
				DatagramPacket packet = ClientController.sendOrRecievePacket(false, 
											new DatagramPacket(data, data.length));
				byte[] dataFromPeer = packet.getData();
				// Get an input stream on the byte array
				// containing the data
				InputStream byteArrayInputStream = new ByteArrayInputStream(dataFromPeer);
				audioInputStream = new AudioInputStream(byteArrayInputStream, format,
						dataFromPeer.length / ClientController.format.getFrameSize());
				
				int cnt = 0;
				byte tempBuffer[] = new byte[1024];
				while ((cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1) {
					if (cnt > 0) {
						// Write data to the internal buffer of
						// the data line where it will be
						// delivered to the speaker.
						speakers.write(tempBuffer, 0, cnt);
						System.out.println("ULAZAK ZVUCNICI");
					} // end if
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Block and wait for internal buffer of the
			// data line to empty.
			speakers.drain();
			speakers.close();
		}
	}
}
