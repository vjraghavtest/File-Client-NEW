import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

public class FileClient {

	public static String HOST = "127.0.0.1";
	public static int PORT = 5555;

	public static void main(String[] args) {
		Socket socket = null;
		String name = null, cmd = null, checksum = null, filename = null;
		// PrintWriter printWriter = null;
		BufferedOutputStream outputStream = null;
		BufferedReader socketReader = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String details = null;
		File file = null;

		// start client
		System.out.println("Starting client");
		try {
			socket = new Socket(HOST, PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Server is not up");
			System.exit(0);
			// e.printStackTrace();
		}

		System.out.println("Client started");
		System.out.println("Client connected");
		// client name
		try {
			System.out.println("Enter name");
			name = reader.readLine();
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println("Server disconnected");
		}

		// send name

		try {
			// printWriter = new PrintWriter(socket.getOutputStream());
			outputStream = new BufferedOutputStream(socket.getOutputStream());
			// printWriter.println(name);
			outputStream.write((name + "\n").getBytes());
			// printWriter.flush();
			outputStream.flush();
			System.out.println("Name sent to server");

			// check for ack
			socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			if (socketReader.readLine().equals("ACK")) {
				System.out.println("ACK RECEIVED");
			} else {
				System.out.println("NO ACK");
			}

			// infinite loop until user enter END
			while (true) {
				try {

					// checking server status
					if (socket.getInputStream() == null)
						break;

					// printing menu
					printMenu();
					cmd = reader.readLine();

					// handling command
					// send command
					if (cmd.startsWith("SEND") || cmd.startsWith("send")) {
						// send command
						// seperate path from cmd
						cmd = cmd.substring(4);
						cmd = cmd.trim();
						cmd = cmd.replace("\"", "");

						// send data
						// checking it is file or not
						System.out.println("Loading file with path " + cmd);
						file = new File(cmd);
						if ((file.isFile())) {
							// sending file details as string to server
							filename = cmd.substring(cmd.lastIndexOf("\\") + 1);
							System.out.println("Calculating checksum");
							checksum = Checksum.getChecksum(cmd);

							// adding prefix denoting file info
							details = "INFOname:" + filename + ",checksum:" + checksum + ",filesize:" + file.length()
									+ ",owner:" + name + "|";
							System.out.println("File Details - " + details);

							// creating object op stream
							// sending object to server
							try {
								System.out.println("Sending file details");
								// printWriter.println(details);
								outputStream.write(details.getBytes());
								// printWriter.flush();
								outputStream.flush();
								System.out.println("File details sent");

								// receiving ack for obj
								if (socketReader.readLine().equals("ACK OBJ")) {
									System.out.println("ACK OBJ received");
								} else {
									System.out.println("No ACK OBJ");
									throw new Exception("NO ACK OBJ");
								}

								// Send the file
								System.out.println("Sending files");
								new FileSender(socket, cmd).start();
								System.out.println("Thread to send data started");

								// printWriter.flush();
								outputStream.flush();

							} catch (Exception e) {
								// e.printStackTrace();
								System.out.println("Connot receive ACK from server");
								break;
							}
						} else {
							System.out.println("File not found");
							//sending echo
							String req="ECHO|";
							outputStream.write(req.getBytes());
							outputStream.flush();
							//receiving echo
							String res=socketReader.readLine();
							System.out.println("Server alive "+res);
						}

					} else if (cmd.equalsIgnoreCase("LIST")) {

						// make string for request
						String req = "LIST|";
						// send request to server
						// printWriter.println(req);
						outputStream.write(req.getBytes());
						// printWriter.flush();
						outputStream.flush();
						// waiting for result
						String res = socketReader.readLine();
						// display file details from strings
						// System.out.println(res);
						// no file
						if (res.equals("")) {
							System.out.println("You have no files to display");
							continue;
						}
						System.out.println("------------------------------------------");
						System.out.println("Filename - Filepath");
						StringTokenizer stringTokenizer = new StringTokenizer(res, "|");
						while (stringTokenizer.hasMoreTokens()) {
							StringTokenizer stringTokenizer2 = new StringTokenizer(stringTokenizer.nextToken(), "<");
							System.out.println(stringTokenizer2.nextToken() + " - " + stringTokenizer2.nextToken());
						}
						System.out.println("--------------------------------------------");

					} else if (cmd.equalsIgnoreCase("END")) {
						System.out.println("Goodbye");
						// printWriter.println("END|");
						outputStream.write("END|".getBytes());
						// printWriter.flush();
						outputStream.flush();
						socket.close();
						break;
					} else {
						//sending echo
						String req="ECHO|";
						outputStream.write(req.getBytes());
						outputStream.flush();
						//receiving echo
						String res=socketReader.readLine();
						System.out.println("Server alive "+res);
					}

				} catch (IOException e) {
					// e.printStackTrace();
					System.out.println("Connot receive ACK from server");
					break;
				}
			}

		} catch (Exception e) {
			System.out.println("Unable to communicate with server");
			try {
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			// e.printStackTrace();
		} finally {
			// close connection at end
			try {
				// objectOutputStream.close();
				// printWriter.close();
				outputStream.close();
				reader.close();
				socket.close();
				System.out.println("Connection closed");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void printMenu() {
		System.out.println("Enter command");
		System.out.println("SEND path - send file");
		System.out.println("LIST - to list all the files");
		System.out.println("END - end connection");
	}

}
