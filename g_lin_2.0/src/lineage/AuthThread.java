package lineage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

public class AuthThread extends Thread {

	public void run() {
		ServerSocket serverSocket = null;
		java.net.Socket socket = null;

		try {
			int authServerPort = 13000;
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress(authServerPort));

			while (true) {
				socket = serverSocket.accept();
				AuthClient authclient = new AuthClient(socket);
				authclient.start();
			}
		} catch (IOException e) {
			System.err.println(e);
		}
	}
}
