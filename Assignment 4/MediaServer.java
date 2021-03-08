//The media server class
import java.net.*;
import java.io.*;
import java.util.*;

public class MediaServer {
	public static byte[] convert(DataPacket pkt) throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
		ObjectOutputStream os = new ObjectOutputStream(new
		BufferedOutputStream(byteStream));
		os.flush();
		os.writeObject(pkt);
		os.flush();
		return byteStream.toByteArray();
	}
	
	public static void main(String[] args) throws IOException {
		DatagramSocket socket = new DatagramSocket(5500);
		int seq = 1;
		byte[] buf = new byte[1000];
		byte[] data = new byte[500];
		System.out.println("Server started!\n");
		// receive request
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);
		System.out.println("Request received!\n");
		// Start sending data
		InetAddress address = packet.getAddress();
		int port = packet.getPort();
		int res = 0;
		while (seq < 1000000) {
			DataPacket pk = new DataPacket(++seq, data,
			System.currentTimeMillis());
			buf = convert(pk);
			packet = new DatagramPacket(buf, buf.length, address, port);
			socket.send(packet);
		}
		socket.close();
	}
}
