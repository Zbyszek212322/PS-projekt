package speed.tester.client;

import speed.tester.multicast.MulticastUDPReceiver;
import speed.tester.multicast.MulticastUDPSender;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainClient {

    static ExecutorService executorService;
    static ClientTCP clientTCP;
    static ClientUDP clientUDP;
    static MulticastUDPReceiver multicastUDPReceiver;
    static MulticastUDPSender multicastUDPSender;

    static String serverName;
    static int port;
    static int dataSize;
    static boolean nagle = false;

    public static void main(String[] args) {
        clientStart();
        clientStop();
    }

    public static void clientStart() {
        Scanner scan = new Scanner(System.in);

        boolean clientFlag = false;
        while (!clientFlag) {

            System.out.println("========> Aplikacja KLIENTA <========");

            boolean ipAddressFlag = false;
            while (!ipAddressFlag) {
                System.out.println("Wprowadź adres servera: ");
                serverName = scan.nextLine();
                if (validIP(serverName)) {
                    ipAddressFlag = true;
                }
            }

            boolean portFlag = false;
            while (!portFlag) {
                System.out.println("Wprowadź numer portu do komunikacji TCP / UDP: ");
                try {
                    port = Integer.parseInt(scan.nextLine());
                } catch (Exception e) {
                    continue;
                }
                if (((port < 0) || (port > 65535))) {
                    System.out.println("To nie jest poprawny numer portu!");
                    continue;
                }
                portFlag = true;
            }

            int dataSizeValidator;
            boolean dataFlag = false;
            while (!dataFlag) {
                System.out.println("Wprowadź wielkość paczki danych w bajtach: ");
                try {
                    dataSizeValidator = Integer.parseInt(scan.nextLine());
                } catch (Exception e) {
                    continue;
                }
                if (((dataSizeValidator >= 1) && (dataSizeValidator <= 65000))) {
                    dataSize = dataSizeValidator;
                    dataFlag = true;
                }
            }

            char isNagle = 'n';
            boolean nagleFlag = false;
            while (!nagleFlag) {
                System.out.println("Czy włączyć algorytm Nagle'a [t/n]: ");
                isNagle = scan.next().charAt(0);
                if (((isNagle == 't') || (isNagle == 'n'))) {
                    nagleFlag = true;
                }
            }
            if (isNagle == 't') {
                nagle = true;
                System.out.println("Algorytm Nagle'a włączony.");
            } else {
                System.out.println("Algorytm Nagle'a wyłączony.");
            }

            try {
                clientTCP = new ClientTCP(serverName, port, dataSize, nagle);
                clientUDP = new ClientUDP(serverName, port, dataSize, nagle);
                multicastUDPReceiver = new MulticastUDPReceiver("230.1.0.10", 9999);
                multicastUDPSender = new MulticastUDPSender("230.1.0.10", 9999, "DISCOVER");
                ExecutorService executorService = Executors.newFixedThreadPool(4);
                executorService.submit(clientTCP);
                executorService.submit(clientUDP);
                executorService.submit(multicastUDPReceiver);
                executorService.submit(multicastUDPSender);
                executorService.shutdown();

            } catch (Exception e) {
                System.out.println("Niepoprawne dane!");
                continue;
            } finally {
                System.out.println("-------------------------------------");
            }
            clientFlag = true;
        }
    }

    public static void clientStop() {
        Scanner scan = new Scanner(System.in);

        boolean finalFlag = false;

        while (!finalFlag) {
            String order = scan.nextLine();
            if (order.equalsIgnoreCase("q")) {
                clientTCP.terminateConnection();
                clientUDP.terminateConnection();
                multicastUDPReceiver.terminateConnection();

                order = "";
            }
            if (order.equalsIgnoreCase("w")) {

                clientTCP = new ClientTCP(serverName, port, dataSize, nagle);
                clientUDP = new ClientUDP(serverName, port, dataSize, nagle);
                multicastUDPReceiver = new MulticastUDPReceiver("230.1.0.10", 9999);
                multicastUDPSender = new MulticastUDPSender("230.1.0.10", 9999, "DISCOVER");
                executorService = Executors.newFixedThreadPool(4);
                executorService.submit(clientTCP);
                executorService.submit(clientUDP);
                executorService.submit(multicastUDPReceiver);
                executorService.submit(multicastUDPSender);
                executorService.shutdown();
                order = "";
            }
        }
    }

    public static boolean validIP(String ip) {
        try {
            if ( ip == null || ip.isEmpty() ) {
                return false;
            }

            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }

            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            if ( ip.endsWith(".") ) {
                return false;
            }

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
