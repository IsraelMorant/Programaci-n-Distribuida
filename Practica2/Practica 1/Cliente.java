import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Cliente {

    private static final String IP_COORDINADOR = "172.24.16.1";
    private static final int PUERTO_COORDINADOR = 30000;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in);
             DatagramSocket socket = new DatagramSocket()) {

            InetAddress ip = InetAddress.getByName(IP_COORDINADOR);

            System.out.println("=== Cliente ===");

            while (true) {
                System.out.print("Nombre del archivo (o salir): ");
                String nombre = scanner.nextLine();

                if (nombre.equalsIgnoreCase("salir")) break;

                String mensaje = "CREAR:" + nombre;
                byte[] datos = mensaje.getBytes(StandardCharsets.UTF_8);

                DatagramPacket paquete = new DatagramPacket(datos, datos.length, ip, PUERTO_COORDINADOR);
                socket.send(paquete);

                byte[] buffer = new byte[1024];
                DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length);
                socket.receive(respuesta);

                System.out.println("Respuesta: " +
                        new String(respuesta.getData(), 0, respuesta.getLength()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
