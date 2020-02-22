package speed.tester.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import static java.lang.Thread.sleep;

public class ClientUDP extends Client {

    DatagramSocket clientSocket;

    public ClientUDP(String serverName, int port, int dataSize, boolean nagle) {

        this.serverName = serverName;
        this.port = port;
        this.dataSize = dataSize;
        this.nagle = nagle;
    }

    public void terminateConnection() {
        isConnectionTerminated = true;
    }

    @Override
    public void run() {

        byte[] buffer = getBuffer();
        try {
            clientSocket = new DatagramSocket();
            isConnectionTerminated = false;
            sleep(200L);
            byte[] message1 = (new String("SIZE:" + dataSize)).getBytes();
            DatagramPacket datagramPacket = new DatagramPacket(message1, 0, message1.length, InetAddress.getByName(serverName), port);
            clientSocket.send(datagramPacket);

            datagramPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(serverName), port);
            while (!isConnectionTerminated) {
                sleep(10L);
                clientSocket.send(datagramPacket);
            }
            if (isConnectionTerminated) {
                byte[] transmissionEnd = (new String("FINE").getBytes());
                System.out.println("UDP klient - FINE");
                datagramPacket = new DatagramPacket(transmissionEnd, 0,
                        transmissionEnd.length, InetAddress.getByName(serverName), port);
                clientSocket.send(datagramPacket);
                clientSocket.close();
            }
        } catch (SocketException se) {
            System.out.println("UDP klient - obsługa wyjątku: SocketException.");
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
