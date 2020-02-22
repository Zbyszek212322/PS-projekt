package speed.tester.client;

import java.net.*;
import java.io.*;

import static java.lang.Thread.sleep;

public class ClientTCP extends Client {

    Socket client;

    public ClientTCP(String serverName, int port, int dataSize, boolean nagle) {

        this.serverName = serverName;
        this.port = port;
        this.dataSize = dataSize;
        this.nagle = nagle;
    }

    public void terminateConnection() {
        isConnectionTerminated = true;
        System.out.println("TCP klient - terminateConnection()");
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {
            client = new Socket(serverName, port);
            client.setTcpNoDelay(!nagle);
            System.out.println("TCP klient - połączony z " + client.getRemoteSocketAddress());
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            sleep(200L);
            byte[] message1 = (new String("SIZE:" + dataSize)).getBytes();
            out.write(message1, 0, message1.length);
            byte[] buffer = getBuffer();
            while (!isConnectionTerminated) {
                sleep(10L);
                out.write(buffer, 0, buffer.length);
            }
        } catch (SocketException se) {
            System.out.println("TCP klient - obsługa wyjątku: SocketException.");
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}