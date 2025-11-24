import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPserver {

    public static void main(String[] args) throws Exception {

        boolean running = true;

        // Port, auf dem wir UDP-Pakete empfangen wollen
        int port = 9876;

        byte[] buffer = new byte[1024];

        // Öffnen eines DatagramSockets auf dem gewünschten Port
        DatagramSocket socket = new DatagramSocket(port);
        System.out.println("UDP-Empfänger gestartet auf Port " + port);


        while(running){

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            // Blocking receive:
            // Thread bleibt hier stehen, bis ein Paket eintrifft
            // oder Timeout abläuft
            socket.receive(packet);

            // Wenn wir hier sind: Paket wurde empfangen
            String receivedData = new String(packet.getData(), 0, packet.getLength());

            System.out.println("Paket empfangen!");
            System.out.println("Sender-Adresse: " + packet.getAddress() + ":" + packet.getPort());
            System.out.println("Inhalt: " + receivedData);

            // Antwort senden:
            String replyString = "PONG";
            byte[] replyData = replyString.getBytes();

            // Wir schicken an die Adresse und den Port des Absenders zurück!
            DatagramPacket replyPacket = new DatagramPacket(replyData, replyData.length, packet.getAddress(), packet.getPort());
            
            socket.send(replyPacket);
            System.out.println("PONG zurückgesendet.");

            buffer = new byte[1024];
        }

        // Socket schließen
        socket.close();
        System.out.println("Socket geschlossen. Programm Ende.");
    }
}
