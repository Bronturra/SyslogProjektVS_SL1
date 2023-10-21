package de.hs_mannheim.ffi.vs.syslog.model.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import de.hs_mannheim.ffi.vs.syslog.model.AsciiChars;
import de.hs_mannheim.ffi.vs.syslog.model.StructuredData;
import de.hs_mannheim.ffi.vs.syslog.model.SyslogFinder;
import de.hs_mannheim.ffi.vs.syslog.model.SyslogMessage;

/*
* Client that broadcasts to find a syslog server then sends a message to the found server.
* */
public class SocketClient {
	private static final int TIMEOUT = 2000;
	private byte[] buf = new byte[512];
	private DatagramSocket socket;
	private InetAddress address;

	public static void main(String[] args) throws Exception {
		System.out.println("Starting Client....");
		String syslogIp = "";
		// find the syslog server
		try {
			syslogIp = SyslogFinder.findSyslogServer();
			if (syslogIp == null)
				throw new Exception("No syslog servers found.");
		}
		catch (IOException exception){
			System.out.println(exception.getMessage());
			return;
		}

		// construct a syslog message
		SyslogMessage syslogMessage = new SyslogMessage(
				SyslogMessage.Facility.ALERT,
				SyslogMessage.Severity.INFORMATIONAL,
				new AsciiChars.L255("myHost"),
				new AsciiChars.L048("myAppName"),
				new AsciiChars.L128("myProcId"),
				new AsciiChars.L032("myMsgId"),
				new StructuredData(Arrays.asList(
						new StructuredData.Element("Element 1"),
						new StructuredData.Element("Element 2"),
						StructuredData.Element.newOrigin("myIp", "myEnterpriseId", "mySoftware", "mySwVersion")
				)
				),
				new SyslogMessage.TextMessage("Test Text Message"));

		// send the syslog message
		SocketClient client = new SocketClient();
		client.SendData(syslogIp, syslogMessage.toString());
		client.close();
	}

	// Sends input to syslogIp on port 514
	public void SendData(String syslogIp, String input) {
		System.out.println("Sending Data to Server ");
		try {
			socket = new DatagramSocket();

			socket.setSoTimeout(TIMEOUT);
			DatagramPacket packetOut = new DatagramPacket(input.getBytes("UTF-8"), input.getBytes("UTF-8").length,
					InetAddress.getByName(syslogIp), 514);
			socket.send(packetOut);
		} catch (SocketTimeoutException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			System.err.println(e);
		}
	}

	public void close() {
		socket.close();
	}

}