
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

public class UDPserver {

    public static void main(String[] args) throws Exception {

        // Port, auf dem wir UDP-Pakete empfangen wollen
        int port = 9876;

        // 1. Öffnen eines DatagramSockets auf dem gewünschten Port
        DatagramSocket socket = new DatagramSocket(port);
        System.out.println("UDP-Empfänger gestartet auf Port " + port);

        // 2. Timeout für das blockierende receive()
        //    Wenn innerhalb von 5 Sekunden kein Paket ankommt,
        //    werfen wir eine SocketTimeoutException.
        socket.setSoTimeout(5000); // 5000 ms = 5 Sekunden
        System.out.println("Warte auf UDP-Paket (Timeout: 5 Sekunden)...");

        // 3. Speicher für empfangene Daten vorbereiten
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        try {
            // 4. Blocking receive:
            //    → Thread bleibt hier stehen, bis ein Paket eintrifft
            //    → oder Timeout abläuft
            socket.receive(packet);

            // 5. Wenn wir hier sind: Paket wurde empfangen
            String receivedData = new String(packet.getData(), 0, packet.getLength());

            System.out.println("Paket empfangen!");
            System.out.println("Sender-Adresse: " + packet.getAddress() + ":" + packet.getPort());
            System.out.println("Inhalt: " + receivedData);

            //Antwort senden:
            String replyString = "PONG";
            byte[] replyData = replyString.getBytes();

            // Wir schicken an die Adresse und den Port des Absenders zurück!
            DatagramPacket replyPacket = new DatagramPacket(replyData, replyData.length, packet.getAddress(), packet.getPort());
            
            socket.send(replyPacket);
            System.out.println("PONG zurückgesendet.");

        } catch (SocketTimeoutException e) {
            // 6. Timeout ist abgelaufen
            System.out.println("Timeout: Innerhalb von 5 Sekunden wurde nichts empfangen.");
        }

        // 7. Socket schließen
        socket.close();
        System.out.println("Socket geschlossen. Programm Ende.");
    }
}
