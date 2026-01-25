import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class Coordinador {

    private static final int PUERTO = 30000;

    // Nodo 0 = LOCAL
    private static final String[] IPS = {
            "127.0.0.1", // nodo local
            
            //"192.168.1.21"  // remoto
    };

    private static final int[] PUERTOS = {
            40000, 40001, 40002
    };

    private static int indice = 0;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PUERTO)) {

            while (true) {
                DatagramPacket p = new DatagramPacket(new byte[1024], 1024);
                socket.receive(p);

                InetAddress ipCliente = p.getAddress();
                int puertoCliente = p.getPort();
                String mensaje = new String(p.getData(), 0, p.getLength());

                // Round-robin entre nodos (local incluido)
                int nodo = indice;
                indice = (indice + 1) % IPS.length;

                InetAddress ipNodo = InetAddress.getByName(IPS[nodo]);
                int puertoNodo = PUERTOS[nodo];

                socket.send(new DatagramPacket(
                        mensaje.getBytes(),
                        mensaje.length(),
                        ipNodo,
                        puertoNodo
                ));

                DatagramPacket respNodo = new DatagramPacket(new byte[1024], 1024);
                socket.receive(respNodo);

                socket.send(new DatagramPacket(
                        respNodo.getData(),
                        respNodo.getLength(),
                        ipCliente,
                        puertoCliente
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
