package dk.tb.integration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TestHiddenIframe {

	private String host;
	private int port;
	private int clientsNumber;
	private Set<Socket> sockets = new HashSet<Socket>();
	long totalBytesRecieved = 0;
	
	private int number_of_iterations = 50; 
	private int time_between_iterations = 100; //milliseconds
	
	private int number_of_messages_to_send = 9;
	private int time_between_messages = 100; //milliseconds
	
	public TestHiddenIframe(String host, int port, int clientNumber) {
		this.host = host;
		this.port = port;
		this.clientsNumber = clientNumber;
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
		testhiddenIframe.runTest();

	}

	private void runTest() {

		try {

			initClients();
			long connectTimestampStart = System.currentTimeMillis();
			measureUpdateTime("connect");
			long time_used_for_connect = System.currentTimeMillis()
					- connectTimestampStart;
			System.out.println("Time spent to connect all clients: "
					+ time_used_for_connect + " milliseconds");
			System.out.println("Bytes recieved in CONNECT response for all clients: "
							+ totalBytesRecieved);
			System.out.println("****************************************************************");

			// send messages in waves and measure the time it takes the server to update
			// all clients with the a message
			int iteration = 0;
			int totalAverageTime = 0;
			while (iteration < number_of_iterations) {
				
				int i = 0;
				while (i < number_of_messages_to_send) {
					i++;
					String message = "message" + i;
					sendMessage(message);
					long sendTimestampstart = System.currentTimeMillis();
					measureUpdateTime("update");
					long time_used_for_update = System.currentTimeMillis()-sendTimestampstart;
					// printResponse();
					System.out.println("Time needed to update all clients with message "
									+ message
									+ " : "
									+ time_used_for_update
									+ " milliseconds");
					System.out.println("Bytes recieved in UPDATE response for all clients: "
									+ totalBytesRecieved);
					System.out.println("****************************************************************\r\n");
					//send the next message after 1 sek
					Thread.sleep(time_between_messages);
				}
				//start the next wave of messages after 5 sek
				Thread.sleep(time_between_iterations);
				iteration++;
			}

		} catch (Exception e) {
			System.err.println(e);
		}

	}

	private void measureUpdateTime(String operation) throws IOException {
		//int updatedClients = 0;
		ArrayList updatedClientsArray = new ArrayList(sockets.size());
		// Find the timestamp when all clients are updated
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
						//updatedClients++;
						updatedClientsArray.add(client.hashCode());
					}
				}
			}
		}
	}

	
	private void sendMessage(String message) throws IOException {
		System.out.println("sending message: " + message);
		totalBytesRecieved = 0;
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

	private void initClients() {

		int counter = 0;
		try {
			while (counter < clientsNumber) {
				counter++;
				StringBuffer req = new StringBuffer();
				req.append("GET /iFrame?Connect HTTP/1.1"
						+ "\r\n"
						+ "Accept:application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5"
						+ "\r\n"
						+ "Accept-Charset:ISO-8859-1,utf-8;q=0.7,*;q=0.3"
						+ "\r\n" + "Connection:keep-alive" + "\r\n" + "Host:"
						+ host + ":" + port + "\r\n" + "Referer:http://" + host
						+ ":" + port + "/iFrame.html" + "\r\n\r\n");
				Socket socket = null;
				try {
					// create socket and connect
					socket = new Socket(host, port);
					sockets.add(socket);
					PrintStream out = new PrintStream(socket.getOutputStream());
					out.print(req); // send bytes in default encoding
					out.flush();
				} catch (Exception e) {
					System.err.println(e);
				}
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}

}
