package de.hs_mannheim.ffi.vs.syslog.model.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 * syslog-Server which receives the data form the client and print them on the
 * console with the client IP-Address. the server and client broadcast messages
 * to discover the server IP-Address .
 * 
 *
 */
public class BroadcastingServer extends Thread {

	/**
	 * Server IP-Address
	 */
	private static String HOST;

	/**
	 * Message sent from client to the server .that indicates the client requesting
	 * the IP-Address of the server
	 */
	private static String REQUESTMSG = "Hallo Server";
	private static DatagramSocket socket = null;
	private byte[] buf = new byte[512];
	private static final int PORT = 4445;

	public static void main(String[] args) throws Exception {
		BroadcastingServer bs = new BroadcastingServer();
		getServerData();
		bs.run();
		close();
	}

	public BroadcastingServer() throws IOException {
		socket = new DatagramSocket(null);
		socket.setReuseAddress(true);
		socket.bind(new InetSocketAddress(PORT));
	}

	/**
	 * finds the IP-address of the server to be broadcasted.
	 * 
	 * @throws UnknownHostException
	 */
	public static void getServerData() {
		InetAddress ip = null;
		String hostname;
		try {
			ip = InetAddress.getLocalHost();
			hostname = ip.getHostName();
			System.out.println("Server current Hostname : " + hostname);
			System.out.println("Server current IP : " + Inet4Address.getLocalHost().getHostAddress());
			HOST = Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * run the server, handle the client messages.
	 */
	public void run() {
		while (true) {
			try {
				DatagramPacket packetIn = new DatagramPacket(buf, buf.length);
				socket.receive(packetIn);
				InetAddress address = packetIn.getAddress();
				int port = packetIn.getPort();
				if (new String(packetIn.getData(), StandardCharsets.UTF_8).contains(REQUESTMSG)) {
					sendIPToClient(address, port);
				} else {
					System.out.println("IP: " + packetIn.getAddress() + " "
							+ new String(packetIn.getData(), StandardCharsets.UTF_8).trim());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * send the Server IP-address to the client.
	 * 
	 * @param address the address of the client, who requested the server info.
	 * @param port    the port of the client, who requested the server info.
	 * @throws IOException
	 */
	private void sendIPToClient(InetAddress address, int port) throws IOException {
		DatagramPacket packet = new DatagramPacket(HOST.getBytes(), HOST.length(), address, port);
		socket.send(packet);
	}

	/**
	 * closes the socket
	 */
	public static void close() {
		socket.close();
	}
}