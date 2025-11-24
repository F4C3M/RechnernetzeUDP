import java.net.*;

public class UDPServerGoBackN {

    public static void main(String[] args) throws Exception {

        int port = 9876;
        boolean running = true;
        DatagramSocket socket = new DatagramSocket(port);
        System.out.println("UDP Server Go-Back-N gestartet auf Port " + port);

        int erwarteteSequenz = 0;
        int maxSequenzNummer = 256;

        while (running) {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            String erhalteneDaten = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Empfangen: " + erhalteneDaten);

            int sequenzNummer = Integer.parseInt(erhalteneDaten.split(" ")[3]);

            // ein "if" für die "verlorenen Pakete" (20% Chance) (T)
            if (Math.random() < 0.2) {
                System.out.println("Simulierter Paketverlust! Kein ACK gesendet.");
                continue;
            }

            if (sequenzNummer == erwarteteSequenz % maxSequenzNummer) {
                String antwort = "PONG SEQ " + sequenzNummer;
                DatagramPacket replyPacket = new DatagramPacket(antwort.getBytes(), antwort.getBytes().length, packet.getAddress(), packet.getPort());
                socket.send(replyPacket);
                
                System.out.println("ACK gesendet: " + antwort);
                erwarteteSequenz++;
            } else {
                int letzterAck = (erwarteteSequenz - 1 + maxSequenzNummer) % maxSequenzNummer;
                String antwort = "PONG SEQ " + letzterAck;
                DatagramPacket replyPacket = new DatagramPacket(antwort.getBytes(), antwort.getBytes().length, packet.getAddress(), packet.getPort());
                socket.send(replyPacket);
                
                System.out.println("Falsches Paket, erneut letztes ACK gesendet: " + antwort);
            }
        }

        // Socket schließen
        socket.close();
        System.out.println("Socket geschlossen. Programm Ende.");
    }
}