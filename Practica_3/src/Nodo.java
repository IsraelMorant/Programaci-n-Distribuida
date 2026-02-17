import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.webserver.WebServer;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URL;

public class Nodo {

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        int puerto = 8081; 

        try {
            // 1. Iniciamos servidor para recibir archivos
            WebServer webServer = new WebServer(puerto);
            XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
            PropertyHandlerMapping phm = new PropertyHandlerMapping();
            phm.addHandler("Nodo", Nodo.class);
            xmlRpcServer.setHandlerMapping(phm);
            webServer.start();

            String miIp = InetAddress.getLocalHost().getHostAddress();
            String miUrl = "http://" + miIp + ":" + puerto + "/xmlrpc";

            System.out.println("=========================================");
            System.out.println("[NODO] Iniciado en: " + miUrl);
            System.out.println("[NODO] Buscando al Balanceador...");

            // 2. Buscar Balanceador y registrarse
            String ipBalanceador = descubrirBalanceador();

            if (ipBalanceador != null) {
                System.out.println("[NODO] Balanceador encontrado en " + ipBalanceador);
                registrarEnBalanceador(ipBalanceador, miUrl);
            } else {
                System.out.println("[NODO] ERROR: No se encontró Balanceador.");
            }
            System.out.println("=========================================");

        } catch (Exception e) {
            System.err.println("Error fatal: " + e.getMessage());
        }
    }

    // --- CÓDIGO MULTICAST (Tus apuntes exactos) ---
    private static String descubrirBalanceador() {
        try {
            // Creamos el MulticastSocket
            MulticastSocket s = new MulticastSocket();
            // Creamos el grupo multicast:
            InetAddress group = InetAddress.getByName("231.0.0.1");
            
            byte[] msj = "BUSCANDO".getBytes();
            // Crear el Datagrama (mensaje, tamaño msj, grupo Multicast y puerto):
            DatagramPacket dgp = new DatagramPacket(msj, msj.length, group, 10000);
            // Enviamos el paquete
            s.send(dgp);

            // Esperar respuesta
            s.setSoTimeout(5000);
            byte[] buffer = new byte[256];
            DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length);
            s.receive(respuesta);
            
            String ipEncontrada = respuesta.getAddress().getHostAddress();
            // Cerramos el socket:
            s.close();
            
            return ipEncontrada;
        } catch (Exception e) { return null; }
    }

    private static void registrarEnBalanceador(String ipBalanceador, String miUrl) {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL("http://" + ipBalanceador + ":9000/xmlrpc"));
            XmlRpcClient cliente = new XmlRpcClient();
            cliente.setConfig(config);
            Object[] parametros = new Object[]{miUrl};
            cliente.execute("Gestor.registrarNodo", parametros);
            System.out.println("[NODO] ¡Registrado con éxito!");
        } catch (Exception e) { System.err.println("Error al registrar: " + e.getMessage()); }
    }

    // --- LÓGICA DE GUARDADO FÍSICO ---
    public int guardarEnDisco(String nombre) {
        String carpeta = "./archivos_nodo";
        File dir = new File(carpeta);
        dir.mkdirs();

        int cont = 0;
        for (File f : dir.listFiles()) { if (f.isFile()) cont++; }

        if (cont >= 3) return 3; // Límite alcanzado

        try {
            if (new File(carpeta, nombre).createNewFile()) {
                System.out.println("[NODO] Archivo guardado: " + nombre);
                return 1;
            } else {
                return 2;
            }
        } catch (IOException e) { return 0; }
    }
}