package speed.tester.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public class MulticastUDPReceiver implements Runnable {

    private boolean isReceiving = true;
    private MulticastSocket multicastSocket;
    private String group;
    private int port;
    private static int serverPort = -1;

    public MulticastUDPReceiver(String group, int port) {

        this.group = group;
        this.port = port;
    }

    @Override
    public void run() {

        try {
            this.multicastSocket = new MulticastSocket(this.port);
            this.multicastSocket.setReuseAddress(true);
            this.multicastSocket.setSoTimeout(1000);
            this.multicastSocket.joinGroup(InetAddress.getByName(this.group));
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] buffer = new byte[1024];
        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);

        System.out.println("MulticastUDP rozpoczyna nasłuch: " + this.group + ":" + this.port);

        while (this.isReceiving) {
            try {
                try {
                    this.multicastSocket.receive(datagramPacket);
                } catch (SocketException e) {
                    break;
                }
                String message = new String(datagramPacket.getData(), datagramPacket.getOffset(), datagramPacket.getLength());
                if(message.equals("DISCOVER")) {
                    MulticastUDPSender multicastSender = new MulticastUDPSender(this.group, this.port, "OFFER:" + serverPort);
                    multicastSender.run();
                } else {
                    String[] msg = (new String(datagramPacket.getData(), datagramPacket.getOffset(), datagramPacket.getLength())).split(":");
                    if(msg[0].startsWith("OFFER")) {
                        //String address = datagramPacket.getAddress().toString();
                        serverPort = (new Integer(msg[1])).intValue();
                        if(serverPort != -1) {
                            System.out.println("MulticastUDP - dostępny port TCP/UDP: " //+ address + ":"
                                    + serverPort);
                        }
                    }
                }
            } catch (IOException e) {
                if (!e.getMessage().equalsIgnoreCase("Receive timed out")) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("MulticastUDP kończy nasłuch.");
    }

    public void setServerPort(int serverPort) {

        this.serverPort = serverPort;
    }

    public void terminateConnection() {
        this.isReceiving = false;
        this.serverPort = -1;
        try {
            multicastSocket.leaveGroup(InetAddress.getByName(this.group));
            multicastSocket.close();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
