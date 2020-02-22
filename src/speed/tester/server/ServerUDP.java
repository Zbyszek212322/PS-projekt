package speed.tester.server;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class ServerUDP extends Server {

    ExecutorService executorService2;
    private DatagramSocket datagramSocket;
    ServerUDPTemplate template;

    public ServerUDP(int port) {
        this.port = port;
    }

    public void terminateConnection() {
        isConnectionTerminated = true;
        System.out.println("UDP serwer - terminateConnection()");
        datagramSocket.close();
    }

    @Override
    public void run() {

        try {
            datagramSocket = new DatagramSocket(port);
            byte[] msg = new byte[65500];

            while(true) {
                DatagramPacket datagramPacket = new DatagramPacket(
                        msg,
                        msg.length
                );
                datagramSocket.receive(datagramPacket);

                if(isFirstMessage) {
                    String[] message1 = (new String(datagramPacket.getData(), datagramPacket.getOffset(),
                            datagramPacket.getLength())).split(":");
                    System.out.println("UDP serwer ---> " + message1[0] + ":" + message1[1]);

                    if(!message1[0].equalsIgnoreCase("SIZE")) {
                        continue;
                    }
                    receivedDataSize = (new Integer(message1[1])).intValue();

                    template = new ServerUDPTemplate(receivedDataSize);
                    template.setIsStopped(false);
                    executorService2 = Executors.newFixedThreadPool(10);
                    executorService2.execute(template);

                    isFirstMessage = false;
                    startTime = System.currentTimeMillis();
                }
                String messageFine = (new String(datagramPacket.getData(), datagramPacket.getOffset(),
                        datagramPacket.getLength()));

                if(messageFine.equalsIgnoreCase("FINE")) {
                    template.setIsStopped(true);
                    executorService2.shutdown();
                    System.out.println("UDP serwer - FINE");
                    System.out.println("UDP serwer: odebrano " + (summaryDataSize/1024.0D) + "kb danych w czasie "
                            + (transmissionTime / 1000.0D) + "s z prędkością " + transmissionSpeed + "kb/s");
                    System.out.println("UDP serwer: poprawność otrzymanych danych " + (((summaryDataSize + receivedDataSize)
                            / template.getTemplateCounter()) * 100) + "%");
                    resetDataCounter();
                }

                dataCounter();
            }
        } catch (SocketException e) {
            System.out.println("UDP serwer - obsługa wyjątku SocketException.");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
