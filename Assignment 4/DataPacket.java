//The data packet class
import java.io.*;

public class DataPacket implements Serializable {
	public DataPacket(int seq, byte[] data, long time) {
		this.seq = seq;
		this.data = data;
		this.time = time;
	}

	int seq;
	long time;
	byte[] data;

	public static void main(String[] args) {}
}
