import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class UDPclient {

    public static void main(String[] args) throws Exception {
        
        // Ziel-Port (muss mit Timer.java übereinstimmen)
        int serverPort = 9876;
        String serverHost = "localhost"; // "localhost" weil gleicher Rechner

        // 1. Socket erstellen (kein fester Port nötig, System sucht einen freien)
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000); // Wir warten max 5 Sek auf das Pong

        try {
            // 2. Daten vorbereiten ("PING")
            String message = "PING";
            byte[] sendData = message.getBytes();
            InetAddress address = InetAddress.getByName(serverHost);

            // 3. Paket schnüren (Daten, Länge, Ziel-IP, Ziel-Port)
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, serverPort);

            // 4. Absenden
            System.out.println("Sende an " + serverHost + ":" + serverPort + " > " + message);
            socket.send(sendPacket);

            // 5. Auf Antwort ("PONG") warten
            byte[] receiveBuffer = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

            System.out.println("Warte auf Antwort (Pong)...");
            
            // Blockiert, bis Antwort kommt oder Timeout feuert
            socket.receive(receivePacket);

            // 6. Antwort auswerten
            String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("Antwort erhalten: " + response);
            System.out.println("Vom Server: " + receivePacket.getAddress() + ":" + receivePacket.getPort());

        } catch (SocketTimeoutException e) {
            System.out.println("Fehler: Keine Antwort (Pong) erhalten (Timeout).");
        } finally {
            // 7. Aufräumen
            socket.close();
            System.out.println("Client beendet.");
        }
    }
}