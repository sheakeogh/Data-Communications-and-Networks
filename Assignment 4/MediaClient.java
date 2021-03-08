// The media client class
import java.io.*;
import java.net.*;
import java.util.*;

public class MediaClient {
	public static DataPacket convert(byte[] buf) throws Exception{
		ByteArrayInputStream byteStream = new ByteArrayInputStream(buf);
		ObjectInputStream is = new ObjectInputStream(new
		BufferedInputStream(byteStream));
		DataPacket pk = (DataPacket)is.readObject();
		is.close();
		return pk;
	}

	public static void main(String[] args) throws Exception {
		DatagramSocket socket = new DatagramSocket();
		int currentSequenceNo = 0;
		int prevSequenceNo = 0;
		long prevDelay = 0;
		// send request
		byte[] buf = new byte[1000];
		InetAddress address = InetAddress.getLocalHost();
		DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 5500);
		socket.send(packet);
		long statInterval = 1000; // 1s
		long lastTime = System.currentTimeMillis();
		// get response
		while(currentSequenceNo < 1000000) {
			packet = new DatagramPacket(buf, buf.length);
			socket.receive(packet);
			DataPacket pk = convert(packet.getData());
			currentSequenceNo = pk.seq;
			long currentTime = System.currentTimeMillis();
			if(currentTime == lastTime + statInterval) {
				//Compute and display network parameters (throughput, loss, delay, jitter)
				long throughput = (((pk.data.length)*(8)*(currentSequenceNo-prevSequenceNo))/(currentTime-lastTime));
				long loss = ((100)-(((currentSequenceNo)/(pk.seq))*(100)));
				long delay = (currentTime-pk.time);
				long jitter = (delay-prevDelay);
				prevDelay = delay;
				System.out.println("Throughput: " + throughput + "bps \t Loss Rate: " + loss + "% \t Packet Delay: " + delay + "ms \t Jitter: " + jitter + "ms");
				lastTime = currentTime;
				prevSequenceNo = currentSequenceNo;
			}
		}
		socket.close();
	}
}
