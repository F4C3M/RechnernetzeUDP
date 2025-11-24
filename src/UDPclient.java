import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class UDPclient {

    public static void main(String[] args) throws Exception {
        
        // Ziel-Port (muss mit UDPserver.java übereinstimmen)
        int serverPort = 9876;
        String serverHost = "localhost"; // "localhost" weil gleicher Rechner
        int anzahlPings = 5; // hab ich mal auf 5 erhöht (T)

        // Socket erstellen (kein fester Port nötig, System sucht einen freien)
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(3000); // Wir warten max 3 Sek auf das Pong

        // IP-Adresse des Servers herausfinden, localhost = 127.0.0.1
        InetAddress serverAddress = InetAddress.getByName(serverHost);

        byte[] receiveBuffer = new byte[1024];
        int sequenzNummer = 0; // Start-Sequenznummer (T)


        System.out.println("Sende Pings (" + anzahlPings + " Versuche)...");

        for (int i = 1; i <= anzahlPings; i++) {
            boolean ackEmpfangen = false; // für die ACKs (T)

            // eine "while" damit wir überpprüfen, ob schon ein PONG empfangen wurde
            while(!ackEmpfangen) {
                // Ping-Paket schnüren
                String message = "PING " + i + " SEQ " + sequenzNummer;
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
                    // eine "if" zum prüfen, ob die ACK / Acknowledgment die richtige SEQ / Sequenznummer hat (T)
                    if (response.contains("SEQ " + sequenzNummer)) {
                        System.out.println(i + ". Versuch: Antwort " + response + " erhalten. RTT: " + rtt + " ms");
                        ackEmpfangen = true;
                        sequenzNummer = 1 - sequenzNummer; // Sequenznummer wechseln 0->1, 1->0
                    } else {
                        System.out.println("Falschen ACK erhalten, erneut senden...");
                    }
                } catch (SocketTimeoutException e) {
                    // Falls das Paket verloren ging
                    System.out.println(i + ". Versuch: Zeitüberschreitung (Paket verloren).");
                }
            }
        }

        socket.close();
        System.out.println("Fertig.");
    }
}