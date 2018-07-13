import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class FileSender extends Thread {
	Socket socket = null;
	String path = null;

	public FileSender(Socket socket, String path) {
		this.socket = socket;
		this.path = path;

	}

	public void run() {
		byte[] buffer = new byte[8 * 1024];
		DataOutputStream dataOutputStream = null;
		FileInputStream fileInputStream = null;
		DataInputStream inputStream = null;
		BufferedReader reader = null;
		File file = null;
		String msg = null;
		int bytesRead = 0;
		try {
			// init
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// socket op stream
			System.out.println("Preparing to send data");
			dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			System.out.println("Output stream loaded");

			// ip stream for file reading
			System.out.println("Loading file");
			file = new File(path);
			fileInputStream = new FileInputStream(file);
			inputStream = new DataInputStream(fileInputStream);
			System.out.println("File prepared");

			// send file
			System.out.println("Started to send file");
			while (true) {
				// reading from file
				// System.out.println("Reading from file");
				bytesRead = inputStream.read(buffer);
				// System.out.println(bytesRead+" bytes readed from file");

				// EOF
				if (bytesRead < 0) {
					System.out.println("EOF reached");
					break;
				}

				// writing into stream
				// System.out.println("Writing into stream");
				dataOutputStream.write(buffer, 0, bytesRead);
				// System.out.println(bytesRead+" bytes sent");
			}
			System.out.println("Flushing stream");
			dataOutputStream.flush();
			System.out.println("File sent");

			// receiving ack from server
			msg = reader.readLine();
			if (msg.startsWith("SUCCESS")) {
				System.out.println("File transfer success");
				System.out.println("File stored at " + msg.substring(8));
			} else {
				System.out.println("File transfer failed");
			}
			System.out.println("Enter the file path");
			fileInputStream.close();
			inputStream.close();
			dataOutputStream.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
