package dk.tb.integration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TestWebSocket extends TestServerImplementation {

	public TestWebSocket(String host, int port, int clientNumber) {
		super(host, port, clientNumber);
		connectRequest =
		"GET / HTTP/1.1"
		+ "\r\n"
		+"Upgrade: WebSocket"
		+ "\r\n"
		+"Connection: Upgrade"
		+ "\r\n"
		+"Host: "+ host + ":" + port 
		+ "\r\n"
		+"Origin: http://"+ host + ":" + port 
		+ "\r\n"
		+"Sec-WebSocket-Key1: 3 201 1F555 65"
		+ "\r\n"
		+"Sec-WebSocket-Key2: 197 5 8    8=    9 806"
		+ "\r\n"
		+ "\r\n"
		+"œ)nøîB..";
		
	}

	/**
	 * @param args
	 * host port client number
	 */
	public static void main(String[] args) {
		// read arguments
		if (args.length != 3) {
			System.out.println("Usage: java TestHiddenIframe <host> <port> <client number>");
			System.exit(-1);
		}
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		int numberOfClients = Integer.parseInt(args[2]);

		TestWebSocket testhiddenIframe = new TestWebSocket(host, port,numberOfClients);
		System.out.println("********** TestWebsocket started **********");
		testhiddenIframe.runTest();
		System.out.println("********** TestWebsocket finished **********");
		
	}

	public void verifyClientsUpdate(String operation) throws IOException {
		ArrayList updatedClientsArray = new ArrayList(sockets.size());
		// make sure all clients are updated
		while (updatedClientsArray.size() < sockets.size()) {
			for (Socket client : sockets) {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						client.getInputStream()));
				StringBuilder builder = new StringBuilder();

				if (!updatedClientsArray.contains(client.hashCode())) {

					if (client.getInputStream().available() > 0) {

						if (operation.equalsIgnoreCase("connect")) {
							while (builder.indexOf("\r\n\r\n") == -1) {
								builder.append(Character.toChars(in.read()));
							}
							//read the last line 
							StringBuilder lastLineBuilder = new StringBuilder();
							while (lastLineBuilder.length()< 16) {
								lastLineBuilder.append(Character.toChars(in.read()));
							}
							builder.append(lastLineBuilder);
						} else if (operation.equalsIgnoreCase("update")) {
							// read until we meet the byte 0xFF
							boolean read = true;
							while (read) {
								int b = in.read();
								builder.append(Character.toChars(b));
								if (b == 0xFF){
									read = false;
								}
							}
						}
						totalBytesRecieved += builder.length();
						System.out.println("Recieved response: "
								+ builder.toString());
						updatedClientsArray.add(client.hashCode());
					}
				}
			}
		}
	}

	public void sendMessage(String message) throws IOException {
		
		System.out.println("sending message: " + message);

		//find a client socket to send the message through
		Socket sendSocket = sockets.iterator().next();
		sendSocket.getOutputStream().write(0x00);
		sendSocket.getOutputStream().write(message.getBytes());
		sendSocket.getOutputStream().write(0xFF);
		sendSocket.getOutputStream().flush();
	}


}
