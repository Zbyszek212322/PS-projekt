package speed.tester.server;

import speed.tester.multicast.MulticastUDPReceiver;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainServer {

    static ExecutorService executorService;
    static ServerTCP serverTCP;
    static ServerUDP serverUDP;
    static MulticastUDPReceiver multicastUDPReceiver;

    static int port;

    public static void main(String[] args) {
        serverStart();
        serverStop();
    }

    public static void serverStart() {
        Scanner scan = new Scanner(System.in);

        System.out.println("========> Aplikacja SERWERA <========");

        boolean portFlagServer = false;
        while (!portFlagServer) {
            System.out.println("Wprowadź numer portu do komunikacji TCP / UDP: ");
            try {
                port = Integer.parseInt(scan.nextLine());
                serverTCP = new ServerTCP(port);
                serverUDP = new ServerUDP(port);
                multicastUDPReceiver = new MulticastUDPReceiver("230.1.0.10", 9999);
                multicastUDPReceiver.setServerPort(port);
                executorService = Executors.newFixedThreadPool(3);
                executorService.submit(serverTCP);
                executorService.submit(serverUDP);
                executorService.submit(multicastUDPReceiver);
                executorService.shutdown();
            } catch (Exception e) {
                continue;
            }
            portFlagServer = true;
        }
        System.out.println("-------------------------------------");
    }

    public static void serverStop() {
        Scanner scan = new Scanner(System.in);

        boolean finalFlag = false;

        while (!finalFlag) {
            String order = scan.nextLine();

            if (order.equalsIgnoreCase("s")) {

                serverTCP.terminateConnection();
                serverUDP.terminateConnection();
                multicastUDPReceiver.terminateConnection();
                executorService.shutdown();
                order = "";
            }
        }
    }
}
