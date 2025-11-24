import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class UDPclient {

    public static void main(String[] args) throws Exception {
        
        // Ziel-Port (muss mit UDPserver.java übereinstimmen)
        int serverPort = 9876;
        String serverHost = "localhost"; // "localhost" weil gleicher Rechner
        int anzahlPings = 3;

        // Socket erstellen (kein fester Port nötig, System sucht einen freien)
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(3000); // Wir warten max 3 Sek auf das Pong

        // IP-Adresse des Servers herausfinden, localhost = 127.0.0.1
        InetAddress serverAddress = InetAddress.getByName(serverHost);

        byte[] receiveBuffer = new byte[1024];

        System.out.println("Sende Pings (" + anzahlPings + " Versuche)...");

        for (int i = 1; i <= anzahlPings; i++) {

            // Ping-Paket schnüren
            String message = "PING " + i;
            byte[] sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

            // --- START ZEITMESSUNG ---
            long startTime = System.nanoTime();

            try {
                // Paket senden
                socket.send(sendPacket);
                
                // Warten auf Antwort
                // Code Bleibt hier stehen, bis Atwort oder Timeout
                socket.receive(receivePacket);

                // --- STOPP ZEITMESSUNG ---
                long endTime = System.nanoTime();

                // RTT berechnen (Nanosekunden in Millisekunden umrechnen)
                double rtt = (endTime - startTime) / 1000000.0;

                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println(i + ". Versuch: Antwort " + response + " erhalten. RTT: " + rtt + " ms");

            } catch (SocketTimeoutException e) {
                // Falls das Paket verloren ging
                System.out.println(i + ". Versuch: Zeitüberschreitung (Paket verloren).");

            }
        }

        socket.close();
        System.out.println("Fertig.");
    }
}