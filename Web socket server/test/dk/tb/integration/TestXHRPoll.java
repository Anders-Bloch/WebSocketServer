package dk.tb.integration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TestXHRPoll {

	private String host;
	private int port;
	private int clientsNumber;
	private Set<Socket> sockets = new HashSet<Socket>();
	long totalBytesRecieved = 0;
	long totalUpdateTime = 0;

	private int number_of_iterations = 10;
	private int time_between_iterations = 10; // milliseconds

	private int number_of_messages_to_send = 10;
	private int time_between_messages = 1; // milliseconds

	public TestXHRPoll(String host, int port, int clientNumber) {
		this.host = host;
		this.port = port;
		this.clientsNumber = clientNumber;
	}

	/**
	 * @param args
	 *            host port client number
	 */
	public static void main(String[] args) {
		// read arguments
		if (args.length != 3) {
			System.out
					.println("Usage: java TestHiddenIframe <host> <port> <client number>");
			System.exit(-1);
		}
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		int numberOfClients = Integer.parseInt(args[2]);

		TestXHRPoll testhiddenIframe = new TestXHRPoll(host, port,
				numberOfClients);
		testhiddenIframe.runTest();

	}

	private void runTest() {

		try {

			long connectTimestampStart = System.currentTimeMillis();
			initClients();
			long time_used_for_connect = System.currentTimeMillis()
					- connectTimestampStart;
			System.out.println("Time spent to connect all clients: "
					+ time_used_for_connect + " milliseconds");
			System.out
					.println("****************************************************************");

			// send messages in periodically in waves, and measure the time it takes the server
			// to update all clients with the a message
			int iteration = 0;
			long totalAverageTime = 0;
			while (iteration < number_of_iterations) {

				int i = 0;
				while (i < number_of_messages_to_send) {
					i++;
					String message = i + "message";
					sendMessage(message);
					long sendTimestampstart = System.currentTimeMillis();
					measureUpdateTime("update");
					long time_used_for_update = System.currentTimeMillis()
							- sendTimestampstart;
					totalUpdateTime += time_used_for_update;
					// printResponse();
					System.out
							.println("Time needed to update all clients with message "
									+ message
									+ " : "
									+ time_used_for_update
									+ " milliseconds");
					System.out
							.println("Bytes recieved in UPDATE response for all clients: "
									+ totalBytesRecieved);
					System.out
							.println("****************************************************************\r\n");
					// send the next message after 1 sek
					Thread.sleep(time_between_messages);
				}
				// start the next wave of messages after 5 sek
				Thread.sleep(time_between_iterations);
				iteration++;
			}
			System.out.println("Average update time: " + totalUpdateTime /(number_of_iterations* number_of_messages_to_send));
		} catch (Exception e) {
			System.err.println(e);
		}

	}

	private void measureUpdateTime(String operation) throws IOException {
		// int updatedClients = 0;

		Set<Socket> newSockets = new HashSet<Socket>();
		int counter = 0;
		// Find the timestamp when all clients are updated
		while (counter < sockets.size()) {
			for (Socket client : sockets) {

				if (!client.isClosed()) {

					BufferedReader in = new BufferedReader(
							new InputStreamReader(client.getInputStream()));
					StringBuilder builder = new StringBuilder();

					if (client.getInputStream().available() > 0) {

						if (operation.equalsIgnoreCase("update")) {
							while (builder.indexOf("message") == -1) {
								builder.append(Character.toChars(in.read()));
							}
							
						}

						totalBytesRecieved += builder.length();
						System.out.println("Recieved response: "
								+ builder.toString());
						// updatedClients++;
						newSockets.add(connectClient());
						counter++;
					}
				}
			}
		}
		this.sockets = newSockets;
	}

	private void sendMessage(String message) throws IOException {
		System.out.println("sending message: " + message);
		totalBytesRecieved = 0;

		String sendTextRequest = "GET /xhrLongPolling?text="
				+ message
				+ " HTTP/1.1"
				+ "\r\n"
				+ "Host: "
				+ host
				+ ":"
				+ port
				+ "\r\n"
				+ "Connection: keep-alive"
				+ "\r\n"
				+ "Referer: http://"
				+ host
				+ ":"
				+ port
				+ "/xhrLongPolling.html"
				+ "\r\n"
				+ "User-Agent: Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.16 (KHTML, like Gecko) Chrome/10.0.648.204 Safari/534.16"
				+ "\r\n" + "	Accept: */*" + "\r\n"
				+ "Accept-Encoding: gzip,deflate,sdch" + "\r\n"
				+ "Accept-Language: da-DK,da;q=0.8,en-US;q=0.6,en;q=0.4"
				+ "\r\n" + "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.3"
				+ "\r\n" + "\r\n";
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
				try {
					sockets.add(connectClient());
				} catch (Exception e) {
					System.err.println(e);
				}
			}
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	private Socket connectClient() {

		StringBuffer req = new StringBuffer();
		Socket connSocket = null;

		req.append("GET /xhrLongPolling?Connect HTTP/1.1" + "\r\n" + "Host: "
				+ host
				+ ":"
				+ port
				+ "\r\n"
				+ "Connection: keep-alive"
				+ "\r\n"
				+ "Referer: http://"
				+ host
				+ ":"
				+ port
				+ "/xhrLongPolling.html"
				+ "\r\n"
				+ "User-Agent: Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.16 (KHTML, like Gecko) Chrome/10.0.648.204 Safari/534.16"
				+ "\r\n" + "Accept: */*" + "\r\n"
				+ "Accept-Encoding: gzip,deflate,sdch" + "\r\n"
				+ "Accept-Language: da-DK,da;q=0.8,en-US;q=0.6,en;q=0.4"
				+ "\r\n" + "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.3"
				+ "\r\n\r\n");
		try {
			// create socket and connect
			connSocket = new Socket(host, port);
			PrintStream out = new PrintStream(connSocket.getOutputStream());
			out.print(req); // send bytes in default encoding
			out.flush();
		} catch (Exception e) {
			System.err.println(e);
		}
		return connSocket;

	}

}
