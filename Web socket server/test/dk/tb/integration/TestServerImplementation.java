package dk.tb.integration;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public abstract class TestServerImplementation {

	protected String host;
	protected int port;
	private int clientsNumber;
	protected Set<Socket> sockets = new HashSet<Socket>();
	long totalBytesRecieved = 0;

	private int number_of_iterations = 10;
	private int time_between_iterations = 100; // milliseconds

	private int number_of_messages_to_send = 100;
	private int time_between_messages = 10; // milliseconds

	protected String connectRequest = "";

	public TestServerImplementation(String host, int port, int clientNumber) {
		this.host = host;
		this.port = port;
		this.clientsNumber = clientNumber;
	}

	public void runTest() {

		long totalBytesRecievedInAllIterations = 0;
		long totalUpdateTime = 0;

		try {

			initClients();

			long connectTimestampStart = System.currentTimeMillis();
			verifyClientsUpdate("connect");
			long time_used_for_connect = System.currentTimeMillis()
					- connectTimestampStart;
			System.out.println("Time spent to connect all clients: "
					+ time_used_for_connect + " milliseconds");
			System.out
					.println("Bytes recieved in CONNECT response for all clients: "
							+ totalBytesRecieved);
			System.out
					.println("****************************************************************");

			// send messages in waves and measure the time it takes the server
			// to update
			// all clients with the a message
			int iteration = 0;
			while (iteration < number_of_iterations) {

				System.out.println("Starting iteration " + (iteration + 1));

				totalBytesRecieved = 0;
				int i = 0;
				while (i < number_of_messages_to_send) {
					Thread.sleep(time_between_iterations);
					i++;
					String message = i +"Hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello" 
					+ "Hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello"
					+ "message";
					sendMessage(message);
					long sendTimestampstart = System.currentTimeMillis();
					verifyClientsUpdate("update");
					long time_used_for_update = System.currentTimeMillis()
							- sendTimestampstart;
					if (time_used_for_update == 0) {
						time_used_for_update = 1;
					}

					totalUpdateTime += time_used_for_update;
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

					Thread.sleep(time_between_messages);
				}
				totalBytesRecievedInAllIterations += totalBytesRecieved;
				iteration++;
			}

		} catch (Exception e) {
			System.err.println(e);
		}

		System.out.println("Test Report: ");
		System.out.println("clients: " + clientsNumber);
		System.out.println("Iterations: " + number_of_iterations);
		System.out.println("Messages sent in each Iteration: "
				+ number_of_messages_to_send);
		System.out.println("Total bytes recieved: "
				+ totalBytesRecievedInAllIterations + " bytes");
		System.out.println("Total update time in milliseconds: "
				+ totalUpdateTime + " milliseconds");

		System.out
				.println("Average update time/client: "
						+ totalUpdateTime
						/ (number_of_iterations * number_of_messages_to_send * clientsNumber)
						+ " milliseconds");
		System.out
				.println("Average bytes recieved/client: "
						+ totalBytesRecievedInAllIterations
						/ (number_of_iterations * number_of_messages_to_send * clientsNumber)
						+ " bytes");
		System.out.println("Average bytes recieved per iteration: "
				+ totalBytesRecievedInAllIterations / number_of_iterations
				+ " bytes");
		System.out.println("Update throughput bits/millisecond: "
				+ (totalBytesRecievedInAllIterations * 8) / totalUpdateTime
				+ " bits/millisecond");

	}

	public void initClients() {

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

	protected Socket connectClient() {

		StringBuffer req = new StringBuffer();
		Socket connSocket = null;

		req.append(connectRequest);
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

	public abstract void sendMessage(String message) throws IOException; 
	public abstract void verifyClientsUpdate(String operation) throws IOException;
	
}