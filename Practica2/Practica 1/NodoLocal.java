import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.io.File;

public class NodoLocal {

    private static final int PUERTO = 40000;
    private static final String CARPETA = "./archivos_locales";

    public static void main(String[] args) {
        new File(CARPETA).mkdirs();

        try (DatagramSocket socket = new DatagramSocket(PUERTO)) {
            while (true) {
                DatagramPacket p = new DatagramPacket(new byte[1024], 1024);
                socket.receive(p);

                String msg = new String(p.getData(), 0, p.getLength());
                String respuesta;

                if (msg.startsWith("CREAR:")) {
                    File f = new File(CARPETA, msg.substring(6));
                    respuesta = f.createNewFile()
                            ? "Creado en nodo LOCAL"
                            : "Ya existía (LOCAL)";
                } else {
                    respuesta = "Comando inválido";
                }

                socket.send(new DatagramPacket(
                        respuesta.getBytes(),
                        respuesta.length(),
                        p.getAddress(),
                        p.getPort()
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
