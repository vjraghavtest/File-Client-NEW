import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Checksum {
	public static String getChecksum(String path) {
//		path="D:\\Default Software's-E7450\\text.txt";
//		path = "D:\\Default Software's-E7450\\Drivers\\Video_Driver_49H49_WN32_20.19.15.4377_A03.EXE";
		MessageDigest messageDigest = null;
		File file = new File(path);
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			try {
				messageDigest = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			byte[] fileData = new byte[(int) file.length()];
			int read = 0;
			try {
				while ((read = fileInputStream.read(fileData)) != -1) {
					messageDigest.update(fileData, 0, read);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			byte[] messageData = messageDigest.digest();

			StringBuffer stringBuffer = new StringBuffer("");
			for (int i = 0; i < messageData.length; i++) {
				stringBuffer.append(Integer.toString((messageData[i] & 0xff) + 0x100, 16).substring(1));
			}

			// System.out.println("Digest(in hex format):: " + sb.toString());
			return stringBuffer.toString();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return "Error";

	}
}
