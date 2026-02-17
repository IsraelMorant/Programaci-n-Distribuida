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

public class ServidorNodo {

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true"); 
        
        // Si corres varios en tu misma PC, cambia este puerto a 8082, 8083, etc.
        // Si son PCs distintas, todos pueden usar el 8081.
        int puerto = 8081; 

        try {
            // 1. Levantamos el servidor de archivos local
            WebServer webServer = new WebServer(puerto);
            XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
            PropertyHandlerMapping phm = new PropertyHandlerMapping();
            phm.addHandler("NodoGestor", NodoGestor.class);
            xmlRpcServer.setHandlerMapping(phm);
            webServer.start();
            
            String miIp = InetAddress.getLocalHost().getHostAddress();
            String miUrl = "http://" + miIp + ":" + puerto + "/xmlrpc";
            
            System.out.println("=========================================");
            System.out.println("[NODO] Iniciado en: " + miUrl);
            System.out.println("[NODO] Buscando al Balanceador por Multicast...");
            
            // 2. Buscamos y nos registramos con el Balanceador
            String ipBalanceador = descubrirBalanceador();
            
            if (ipBalanceador != null) {
                System.out.println("[NODO] Balanceador encontrado. Registrando...");
                registrarEnBalanceador(ipBalanceador, miUrl);
            } else {
                System.out.println("[NODO] No se encontró el Balanceador.");
            }
            System.out.println("=========================================");
            
        } catch (Exception e) {
            System.err.println("Error fatal: " + e.getMessage());
        }
    }

    private static String descubrirBalanceador() {
        try {
            InetAddress grupo = InetAddress.getByName("230.0.0.1");
            MulticastSocket socket = new MulticastSocket();
            socket.setInterface(InetAddress.getLocalHost());
            socket.setTimeToLive(5); 
            
            String mensaje = "BUSCANDO_BALANCEADOR";
            DatagramPacket paqueteSalida = new DatagramPacket(mensaje.getBytes(), mensaje.length(), grupo, 4446);
            socket.send(paqueteSalida); 
            
            socket.setSoTimeout(5000); 
            byte[] buffer = new byte[256];
            DatagramPacket paqueteEntrada = new DatagramPacket(buffer, buffer.length);
            socket.receive(paqueteEntrada); 
            
            socket.close();
            return paqueteEntrada.getAddress().getHostAddress();
        } catch (Exception e) { return null; }
    }

    private static void registrarEnBalanceador(String ipBalanceador, String miUrl) {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL("http://" + ipBalanceador + ":9000/xmlrpc"));
            XmlRpcClient cliente = new XmlRpcClient();
            cliente.setConfig(config);
            Object[] parametros = new Object[]{ miUrl };
            cliente.execute("GestorBalanceador.registrarNodo", parametros);
            System.out.println("[NODO] ¡Registro exitoso! Listo para trabajar.");
        } catch (Exception e) { System.err.println("Error al registrar: " + e.getMessage()); }
    }

    // --- LA LÓGICA DE GUARDADO FÍSICO ---
    public static class NodoGestor {
        private static final String RUTA_LOCAL = "./archivos_nodo"; 
        private static final int LIMITE_ARCHIVOS = 3; 

        public int guardarEnDisco(String nombre) {
            int cantidadActual = contarArchivos();
            if (cantidadActual >= LIMITE_ARCHIVOS) { return 3; } // 3 = Lleno
            
            new File(RUTA_LOCAL).mkdirs();
            File archivo = new File(RUTA_LOCAL, nombre);
            try {
                if (archivo.createNewFile()) {
                    System.out.println("[NODO] Archivo guardado: " + nombre);
                    return 1; 
                } else { return 2; }
            } catch (IOException e) { return 0; }
        }

        private int contarArchivos() {
            File[] archivos = new File(RUTA_LOCAL).listFiles();
            int contador = 0;
            if (archivos != null) { for (File f : archivos) { if (f.isFile()) contador++; } }
            return contador;
        }
    }
}