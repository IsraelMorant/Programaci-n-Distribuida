import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.File;

public class Nodo {

    private static final int PUERTO = 40001; // cambia para cada nodo
    private static final String CARPETA = "./nodo1";

    public static void main(String[] args) {
        new File(CARPETA).mkdirs();

        try (DatagramSocket socket = new DatagramSocket(PUERTO)) {
            System.out.println("Nodo activo en puerto " + PUERTO);

            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);

                String mensaje = new String(paquete.getData(), 0, paquete.getLength());

                String respuesta;
                if (mensaje.startsWith("CREAR:")) {
                    String nombre = mensaje.substring(6);
                    File f = new File(CARPETA, nombre);
                    respuesta = f.createNewFile()
                            ? "Archivo creado en nodo"
                            : "Archivo ya existía";
                } else {
                    respuesta = "Comando inválido";
                }

                DatagramPacket resp = new DatagramPacket(
                        respuesta.getBytes(),
                        respuesta.length(),
                        paquete.getAddress(),
                        paquete.getPort()
                );
                socket.send(resp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
