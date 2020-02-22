package speed.tester.multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class MulticastUDPSender implements Runnable {

    private String group;
    private int port;
    private String msg;

    public MulticastUDPSender(String group, int port, String msg) {

        this.group = group;
        this.port = port;
        this.msg = msg;
    }

    @Override
    public void run() {

        try {
            MulticastSocket multicastSocket  = new MulticastSocket();
            byte[] buffer = msg.getBytes(StandardCharsets.UTF_8);
            DatagramPacket pack = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(group), port);
            multicastSocket.send(pack);
            multicastSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
