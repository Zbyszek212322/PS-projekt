package speed.tester.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ServerTCP extends Server{

    private ServerSocket serverSocket;
    private Socket socket;

    public ServerTCP(int port) throws IOException {
        this.port = port;
    }

    public void terminateConnection() {
        isConnectionTerminated = true;
        System.out.println("TCP serwer - terminateConnection()");
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(port, 0, null);
            while (!isConnectionTerminated) {
                socket = serverSocket.accept();
                resetDataCounter();
                System.out.println("TCP serwer - połączony z " + socket.getRemoteSocketAddress());
                DataInputStream in = new DataInputStream(socket.getInputStream());
                byte[] msg = new byte[65000];
                while (!isConnectionTerminated) {
                    int bytes = in.read(msg);
                    if (bytes < 0) {
                        break;
                    }
                    if (isFirstMessage) {
                        String[] message1 = (new String(msg, 0, bytes)).split(":");
                        System.out.println("TCP serwer ---> " + message1[0] + ":" + message1[1]);
                        if (!message1[0].equalsIgnoreCase("SIZE")) {
                            continue;
                        }
                        receivedDataSize = (new Integer(message1[1])).intValue();
                        isFirstMessage = false;
                        startTime = System.currentTimeMillis();
                        continue;
                    }
                    dataCounter();
                }
                socket.close();
                System.out.println("TCP serwer: odebrano " + (summaryDataSize / 1024.0D) + "kb danych w czasie "
                        + (transmissionTime / 1000.0D) + "s z prędkością " + transmissionSpeed + "kb/s");
            }
        } catch (SocketException e) {
            System.out.println("TCP serwer - obsługa wyjątku SocketException.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
