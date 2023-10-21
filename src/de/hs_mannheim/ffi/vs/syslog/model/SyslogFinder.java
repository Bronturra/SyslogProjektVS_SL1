package de.hs_mannheim.ffi.vs.syslog.model;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

import de.hs_mannheim.ffi.vs.syslog.model.server.SocketServer;

/*
 * SyslogFinder broadcasts a special message (SocketServer.BROADCAST_MESSAGE) to all available server (to port 8888)
 * and chooses the first one tha returns the same message.
 * */
public class SyslogFinder {
    public static String findSyslogServer() throws IOException {
        // create socket and data for messages to send and recieve.
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(2000);
        byte[] bufferOut = SocketServer.BROADCAST_MESSAGE.getBytes(), bufferIn = new byte[512];

        // iterate all servers.
        var nics = NetworkInterface.getNetworkInterfaces().asIterator();
        while (nics.hasNext()) {
            var nic = nics.next();
            if (nic.getMTU() > 0) {
                System.out.println(nic);
                System.out.println("MTU=" + nic.getMTU());
                for (var nif : nic.getInterfaceAddresses()) {
                    if (nif.getBroadcast() != null) {
                        System.out.println("\t" + nif.getBroadcast());

                        // once you find a server: send the message and wait for a response
                        DatagramPacket packetOut = new DatagramPacket(bufferOut, bufferOut.length, nif.getBroadcast(), 8888);
                        socket.send(packetOut);
                        DatagramPacket packetIn = new DatagramPacket(bufferIn, bufferIn.length, nif.getBroadcast(), 8888);
                        try {
                            socket.receive(packetIn);
                        } catch (Exception exception) {
                            System.out.println("\t" + exception.getMessage());
                            continue;
                        }
                        String res = new String(packetIn.getData(), StandardCharsets.UTF_8).trim();
                        // if the response contains the same message choose this server and return its ip address.
                        if (res.contains(SocketServer.BROADCAST_MESSAGE)) {
                            socket.close();
                            return nif.getBroadcast().getHostAddress();
                        }
                    }
                }
            }
        }

        socket.close();
        return null;
    }
}
