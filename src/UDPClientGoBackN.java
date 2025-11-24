import java.net.*;
import java.util.*;

public class UDPClientGoBackN {

    public static void main(String[] args) throws Exception {

        int serverPort = 9876;
        String serverHost = "localhost";
        int anzahlPings = 10; // das sind alle unsere PINGs
        int fensterGroesse = 3; // die Größe unseres Fensters in dem wir mehrere PINGs schicken

        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(3000); // Timeout in 3sek Takt
        InetAddress serverAddress = InetAddress.getByName(serverHost);

        int base = 0; // Start für unser Fenster
        int nextSequenzNummer = 0; // nächste Sequenznummer
        int maxSequenzNummer = 256; // maximal mögliche Sequenznummern (wegen 2^8 Bytes)

        Map<Integer, String> gesendetePings = new HashMap<>(); // speichern welche Sequenznummer zu welchem Packet gehört
        Map<Integer, Long> sendTime = new HashMap<>(); // speichern der Sendezeit für die RTT


        while (base < anzahlPings) {
            // senden von Paketen in unserem Fenster
            while (nextSequenzNummer < base + fensterGroesse && nextSequenzNummer < anzahlPings) {
                String paketNachricht = "PING " + (nextSequenzNummer + 1) + " SEQ " + (nextSequenzNummer % maxSequenzNummer);
                byte[] sendData = paketNachricht.getBytes(); // wandlt unsere Nachricht in Bytes um zum senden

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
                socket.send(sendPacket);

                gesendetePings.put(nextSequenzNummer % maxSequenzNummer, paketNachricht);
                sendTime.put(nextSequenzNummer % maxSequenzNummer, System.nanoTime());
                System.out.println("Gesendet: " + paketNachricht);
                nextSequenzNummer++;
            }

            // warter auf unserse ACKs <3
            try {
                byte[] buffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(receivePacket);
                String erhalteneNachricht = new String(receivePacket.getData(), 0, receivePacket.getLength());

                if (erhalteneNachricht.startsWith("PONG SEQ")) {
                    int ackSequenz = Integer.parseInt(erhalteneNachricht.split(" ")[2]);
                    System.out.println("ACK empfangen: " + ackSequenz);

                    while ((base % maxSequenzNummer) != (ackSequenz + 1) % maxSequenzNummer && base < nextSequenzNummer) {
                        long rtt = (System.nanoTime() - sendTime.get(base % maxSequenzNummer)) / 1_000_000;
                        System.out.println("Ping " + (base + 1) + " bestätigt. RTT: " + rtt + " ms");
                        gesendetePings.remove(base % maxSequenzNummer);
                        sendTime.remove(base % maxSequenzNummer);
                        base++;
                    }
                }
                // ein catch, damit Pakete für die noch kein ACK zurückkam nochmal gesendet werden
            } catch (SocketTimeoutException e) {
                System.out.println("Timeout! Gehe zurück und sende alle unbestätigten Pakete erneut.");
                
                for (int sequenz = base; sequenz < nextSequenzNummer; sequenz++) {
                    String erneuteNachricht = gesendetePings.get(sequenz % maxSequenzNummer);
                    byte[] sendData = erneuteNachricht.getBytes();

                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
                    socket.send(sendPacket);
                    sendTime.put(sequenz % maxSequenzNummer, System.nanoTime());
                    System.out.println("Erneut gesendet: " + erneuteNachricht);
                }
            }
        }

        socket.close();
        System.out.println("Alle Pings bestätigt. Fertig.");
    }
}