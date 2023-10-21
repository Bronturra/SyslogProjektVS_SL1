package de.hs_mannheim.ffi.vs.syslog.model.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

import de.hs_mannheim.ffi.vs.syslog.model.ContentOriginator;

/**
 * The client sends data (according to Syslog-Protokoll RFC 5424 standards) to
 * the server. The client request the IP-address of server using broadcast.
 * <p>
 * Note: the server and client should communicate automatically with each other
 * but the firewall settings and having some virus-defender would block the UDP-
 * Broadcast and won't allow the client to find the server. (make sure to check
 * the inBound roles in your firewall defender ).
 * </p>
 * <p>
 * Note: the client runs as a command line program. please use this convention
 * to pass the arguments. facility, severity, message.in other words the facility at index 0,
 * the severity at index 1 and the message at index 2. 
 * </p>
 * 
 * 
 */
public class BroadcastingClient {

	/**
	 * UDP message sent from client to the server and indicates the client
	 * requesting the IP-Address of the server
	 */
	private static String REQUESTMSG = "Hallo Server";
	/**
	 * Standards Broadcasting Address
	 */
	private static final String BROADCASTADDR = "255.255.255.255";
	private static String HOST;
	private static final int PORT = 4445;
	private static final int TIMEOUT = 2000;
	private byte[] buf = new byte[512];
	private static DatagramSocket socket;
	private InetAddress address;

	public static void main(String[] args) throws Exception {
		System.out.println("Starting Client....");
		BroadcastingClient bc = new BroadcastingClient();
		bc.discoverServers(REQUESTMSG);
		ContentOriginator co = new ContentOriginator(args);
		SendData(co.toString());
		bc.close();
	}

	/**
	 * sends Data to the server ;
	 * 
	 * @param input payload of the UDP Datagram-Packet
	 */
	public static void SendData(String input) {
		System.out.println("Sending Data to Server ");
		try {
			socket.setSoTimeout(TIMEOUT);
			InetAddress iaddr = InetAddress.getByName(HOST);
			DatagramPacket packetOut = new DatagramPacket(input.getBytes("UTF-8"), input.getBytes("UTF-8").length,
					iaddr, PORT);
			socket.send(packetOut);
		} catch (SocketTimeoutException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	/**
	 * Constructs a BroadcastingClient object and sets InetAddress of the
	 * BroadcastingClient.
	 * 
	 * @throws Exception
	 */
	public BroadcastingClient() throws Exception {
		this.address = InetAddress.getByName(BROADCASTADDR);
	}

	/**
	 * discover Servers in the same sub-net using broadcasting.
	 * 
	 * @param msg a message to be sent.
	 * @throws IOException
	 */
	public void discoverServers(String msg) throws IOException {
		initializeSocketForBroadcasting();
		copyMessageOnBuffer(msg);
		broadcastPacket(address);
		receivePacket();
	}

	/**
	 * initialize Socket For Broadcasting
	 * 
	 * @throws SocketException
	 */
	private void initializeSocketForBroadcasting() throws SocketException {
		socket = new DatagramSocket();
		socket.setBroadcast(true);
	}

	/**
	 * copy Message On Buffer
	 * 
	 * @param msg the request-message to be sent to the server.
	 */
	private void copyMessageOnBuffer(String msg) {
		buf = msg.getBytes(StandardCharsets.UTF_8);
	}

	/**
	 * broadcast a Packet to the server containing the request-message.
	 * 
	 * @param address the targeted address.
	 * @throws IOException
	 */
	private void broadcastPacket(InetAddress address) throws IOException {
		DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
		socket.send(packet);
	}

	/**
	 * receives IP-Address of the server and set the Host to the received IP.
	 * 
	 * @throws IOException
	 */
	private void receivePacket() throws IOException {
		DatagramPacket packet = new DatagramPacket(new byte[512], 512);
		socket.receive(packet);
		String received = new String(packet.getData(), StandardCharsets.UTF_8).trim();
		HOST = received;
		System.out.println("Server Found and it's IP is: " + received);
	}

	/**
	 * close the socket.
	 */
	public void close() {
		socket.close();
	}

}