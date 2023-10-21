package de.hs_mannheim.ffi.vs.syslog.model.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

// A generic udp server implementation that also supports broadcasts.
public class SocketServer extends Thread {
	public static final String BROADCAST_MESSAGE = "LookingForServer";

	private DatagramSocket socket;
	private int port;
	private boolean isBroadcast;
	private byte[] buf = new byte[512];

	// Create 2 sockets, one for receiving broadcasts and one for receiving Syslog messages.
	public static void main(String[] args) throws Exception {
		SocketServer broadcast = new SocketServer(true, 8888);
		SocketServer udp = new SocketServer(false, 514);
		broadcast.start();
		udp.start();
	}

	// set isBroadcast to true if the server should listen to broadcasts and respond.
	public SocketServer(boolean isBroadcast, int port) throws IOException {
		this.isBroadcast = isBroadcast;
		this.port = port;
		socket = new DatagramSocket(new InetSocketAddress(port));

		if (isBroadcast)
			socket.setBroadcast(true);
	}

	public void run() {
		while (true) {
			try {
				DatagramPacket packetIn = new DatagramPacket(buf, buf.length);
				socket.receive(packetIn);

				// if broadcast and the special code (BROADCAST_MESSAGE) is in the message, respond with the same message.
				if (isBroadcast && new String(packetIn.getData(), StandardCharsets.UTF_8).contains(BROADCAST_MESSAGE)){
					sendIPToClient(packetIn.getAddress(), packetIn.getPort());
				}
				// if not broadcast, print the syslog message onto the console.
				else {
					System.out.println("IP: " + packetIn.getAddress() + " "
							+ new String(packetIn.getData(), StandardCharsets.UTF_8).trim());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// Send a confirmation to the client that this is a syslog server (by sending BROADCAST_MESSAGE).
	private void sendIPToClient(InetAddress address, int port) throws IOException {
		DatagramPacket packet = new DatagramPacket(BROADCAST_MESSAGE.getBytes(), BROADCAST_MESSAGE.length(), address, port);
		socket.send(packet);
	}

	public void close() {
		socket.close();
	}
}