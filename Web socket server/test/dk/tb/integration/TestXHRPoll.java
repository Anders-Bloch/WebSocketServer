package dk.tb.integration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class TestXHRPoll  extends TestServerImplementation {


	
	public TestXHRPoll(String host, int port, int clientNumber) {
		super(host, port, clientNumber);
		connectRequest ="GET /xhrLongPolling?Connect HTTP/1.1" + "\r\n" + "Host: "
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
		+ "\r\n\r\n"; 
	}

	/**
	 * @param args
	 *            host port client number
	 */
	public static void main(String[] args) {
		// read arguments
		if (args.length != 3) {
			System.out
					.println("Usage: java TestXHRPoll <host> <port> <client number>");
			System.exit(-1);
		}
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		int numberOfClients = Integer.parseInt(args[2]);

		TestXHRPoll testhiddenIframe = new TestXHRPoll(host, port,
				numberOfClients);
		System.out.println("********** TestXHRPoll started **********");
		testhiddenIframe.runTest();
		System.out.println("********** TestXHRPoll finished **********");
		
	}

	@Override
	public void measureUpdateTime(String operation) throws IOException {
		if (operation.equalsIgnoreCase("connect")) {
			return;
		}	
		Set<Socket> newSockets = new HashSet<Socket>();
		int counter = 0;
	
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
						newSockets.add(connectClient());
						counter++;
					}
				}
			}
		}
		this.sockets = newSockets;
	}
	@Override
	public void sendMessage(String message) throws IOException {
		System.out.println("sending message: " + message);
		//totalBytesRecieved = 0;

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
//		try {
//			Thread.sleep(1);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		if (sendSocket != null)
			sendSocket.close(); // close connection
	}

	
}
