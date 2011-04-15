package dk.tb.integration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;

public class TestHiddenIframe extends TestServerImplementation {


	public TestHiddenIframe(String host, int port, int clientNumber) {
		super(host, port, clientNumber);
		
		connectRequest = "GET /iFrame?Connect HTTP/1.1"
			+ "\r\n"
			+ "Accept:application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5"
			+ "\r\n"
			+ "Accept-Charset:ISO-8859-1,utf-8;q=0.7,*;q=0.3"
			+ "\r\n" + "Connection:keep-alive" + "\r\n" + "Host:"
			+ host + ":" + port + "\r\n" + "Referer:http://" + host
			+ ":" + port + "/iFrame.html" + "\r\n\r\n";
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

		TestHiddenIframe testhiddenIframe = new TestHiddenIframe(host, port,numberOfClients);
		System.out.println("********** TestHiddenIframe started **********");
		testhiddenIframe.runTest();
		System.out.println("********** TestHiddenIframe finished **********");

	}

	
	
	public void verifyClientsUpdate(String operation) throws IOException {

		ArrayList updatedClientsArray = new ArrayList(sockets.size());
	
		//Make sure all clients are updated
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
						} else if (operation.equalsIgnoreCase("update")) {
							// read first line
							while (builder.indexOf("\r\n") == -1) {
								builder.append(Character.toChars(in.read()));
							}
							// read second line
							StringBuilder builder2Line = new StringBuilder();
							while (builder2Line.indexOf("\r\n") == -1) {
								builder2Line.append(Character.toChars(in.read()));
							}
							builder.append(builder2Line);
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
		//totalBytesRecieved = 0;
		String sendTextRequest = "GET /iFrame?text="
				+ message
				+ " HTTP/1.1"
				+ "\r\n"
				+ "Accept:application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5"
				+ "\r\n" + "Accept-Charset:ISO-8859-1,utf-8;q=0.7,*;q=0.3"
				+ "\r\n" + "Connection:keep-alive" + "\r\n" + "Host:" + host
				+ ":" + port + "\r\n" + "Referer:http://" + host + ":" + port
				+ "/iFrame.html" + "\r\n\r\n";
		Socket sendSocket = new Socket(host, port);
		PrintStream out = new PrintStream(sendSocket.getOutputStream());
		out.print(sendTextRequest);
		out.flush();
		if (sendSocket != null)
			sendSocket.close(); // close connection
	}

}
