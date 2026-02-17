import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.webserver.WebServer;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URL;

public class ServidorNodoMain {

    public static void main(String[] args) {
        // Truco para evitar problemas de IPv6 en Multicast
        System.setProperty("java.net.preferIPv4Stack", "true"); 
        int puerto = 8081; 
        
        try {
            System.out.println("Java está usando la IP local: " + java.net.InetAddress.getLocalHost().getHostAddress());
            // 1. Levantamos el servidor de este Nodo para que pueda RECIBIR archivos
            WebServer webServer = new WebServer(puerto);
            XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
            PropertyHandlerMapping phm = new PropertyHandlerMapping();
            phm.addHandler("NodoGestor", NodoGestor.class);
            xmlRpcServer.setHandlerMapping(phm);
            webServer.start();
            
            // 2. Obtenemos nuestra propia IP real en la red Wi-Fi
            String miIp = InetAddress.getLocalHost().getHostAddress();
            String miUrl = "http://" + miIp + ":" + puerto + "/xmlrpc";
            
            System.out.println("=========================================");
            System.out.println("[NODO] Servidor local iniciado en: " + miUrl);
            System.out.println("[NODO] Buscando al Balanceador en la red...");
            
            // 3. Buscamos al Balanceador por Multicast
            String ipBalanceador = descubrirBalanceador();
            
            if (ipBalanceador != null) {
                // 4. Si lo encontramos, nos "registramos" con él
                System.out.println("[NODO] Balanceador encontrado en " + ipBalanceador + ". Registrándose...");
                registrarEnBalanceador(ipBalanceador, miUrl);
            } else {
                System.out.println("[NODO] No se encontró el Balanceador. Inicie el Balanceador y reinicie este nodo.");
            }
            System.out.println("=========================================");
            
        } catch (Exception e) {
            System.err.println("[NODO] Error fatal: " + e.getMessage());
        }
    }

    // ---- MÉTODOS DE RED PARA EL AUTO-REGISTRO ----

    private static String descubrirBalanceador() {
        try {
            InetAddress grupo = InetAddress.getByName("230.0.0.1");
            MulticastSocket socket = new MulticastSocket();

            

// --- 1. OBLIGAMOS a que el grito salga SÓLO por el Wi-Fi ---
        socket.setInterface(InetAddress.getLocalHost());
        
        // --- 2. AUMENTAMOS la fuerza del paquete para que el router no lo mate ---
        socket.setTimeToLive(5); 
        
        String mensaje = "BUSCANDO_BALANCEADOR";
        DatagramPacket paqueteSalida = new DatagramPacket(mensaje.getBytes(), mensaje.length(), grupo, 4446);
        socket.send(paqueteSalida);
            
            socket.setSoTimeout(5000); // Espera 5 segundos máximo
            byte[] buffer = new byte[256];
            DatagramPacket paqueteEntrada = new DatagramPacket(buffer, buffer.length);
            socket.receive(paqueteEntrada); 
            
            socket.close();
            return paqueteEntrada.getAddress().getHostAddress();
        } catch (Exception e) {
            return null;
        }
    }

    private static void registrarEnBalanceador(String ipBalanceador, String miUrl) {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL("http://" + ipBalanceador + ":9000/xmlrpc"));
            XmlRpcClient cliente = new XmlRpcClient();
            cliente.setConfig(config);
            
            Object[] parametros = new Object[]{ miUrl };
            cliente.execute("GestorBalanceador.registrarNodo", parametros);
            System.out.println("[NODO] ¡Registro exitoso! Listo para recibir archivos.");
        } catch (Exception e) {
            System.err.println("[NODO] Error al registrarse con el Balanceador: " + e.getMessage());
        }
    }
}