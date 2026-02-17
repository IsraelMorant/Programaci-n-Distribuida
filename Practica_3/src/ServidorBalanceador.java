import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.webserver.WebServer;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URL;
import java.util.ArrayList;

public class ServidorBalanceador {

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true"); 
        
        try {
            System.out.println("=========================================");
            System.out.println("   INICIANDO BALANCEADOR CENTRAL");
            System.out.println("=========================================");
            
            // 1. Iniciamos el Radar Multicast
            new Thread(() -> escucharMulticast()).start();

            // 2. Iniciamos el Servidor RPC en el puerto 9000
            WebServer webServer = new WebServer(9000);
            XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
            PropertyHandlerMapping phm = new PropertyHandlerMapping();
            phm.addHandler("GestorBalanceador", GestorBalanceador.class);
            xmlRpcServer.setHandlerMapping(phm);
            webServer.start();
            
            System.out.println("[BALANCEADOR] RPC escuchando en puerto 9000.");
            
        } catch (Exception e) {
            System.err.println("Error fatal al iniciar: " + e.getMessage());
        }
    }

    // --- EL RADAR MULTICAST ---
    private static void escucharMulticast() {
        try {
            MulticastSocket socket = new MulticastSocket(4446);
            InetAddress grupo = InetAddress.getByName("230.0.0.1");
            socket.setInterface(InetAddress.getLocalHost());
            socket.joinGroup(grupo);
            
            System.out.println("[MULTICAST] Radar activo. Esperando Clientes y Nodos...");
            
            while (true) {
                byte[] buffer = new byte[256];
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);
                
                String mensaje = new String(paquete.getData(), 0, paquete.getLength());
                if (mensaje.equals("BUSCANDO_BALANCEADOR")) {
                    String respuesta = "AQUI_ESTOY";
                    DatagramPacket respuestaPaquete = new DatagramPacket(
                        respuesta.getBytes(), respuesta.length(), 
                        paquete.getAddress(), paquete.getPort()
                    );
                    socket.send(respuestaPaquete);
                }
            }
        } catch (Exception e) {
            System.err.println("[MULTICAST] Error: " + e.getMessage());
        }
    }

    // --- LA LÓGICA DE DISTRIBUCIÓN ---
    public static class GestorBalanceador {
        private static ArrayList<String> nodos = new ArrayList<>();
        private static int turnoActual = 0; 

        public boolean registrarNodo(String urlNodo) {
            if (!nodos.contains(urlNodo)) {
                nodos.add(urlNodo);
                System.out.println("[REGISTRO] ¡Nuevo Nodo añadido!: " + urlNodo);
                System.out.println("[REGISTRO] Nodos totales disponibles: " + nodos.size());
            }
            return true;
        }

        public int recibirYDistribuirArchivo(String nombreArchivo) {
            if (nodos.isEmpty()) {
                System.out.println("[ERROR] No hay nodos conectados.");
                return 0; 
            }

            int intentos = 0;
            int maxNodos = nodos.size();

            while (intentos < maxNodos) {
                String urlNodoDestino = nodos.get(turnoActual);
                turnoActual = (turnoActual + 1) % maxNodos;

                System.out.println("[ENRUTANDO] Mandando '" + nombreArchivo + "' a: " + urlNodoDestino);
                int respuestaNodo = enviarANodoDestino(urlNodoDestino, nombreArchivo);
                
                if (respuestaNodo == 1 || respuestaNodo == 2) {
                    return respuestaNodo; 
                } else if (respuestaNodo == 3) {
                    System.out.println(" -> Nodo lleno. Buscando otro...");
                    intentos++;
                } else {
                    System.out.println(" -> Nodo caído. Buscando otro...");
                    intentos++;
                }
            }
            return 3; // Todos llenos
        }

        private int enviarANodoDestino(String urlNodo, String nombreArchivo) {
            try {
                XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                config.setServerURL(new URL(urlNodo));
                XmlRpcClient cliente = new XmlRpcClient();
                cliente.setConfig(config);
                Object[] parametros = new Object[]{ nombreArchivo };
                return (Integer) cliente.execute("NodoGestor.guardarEnDisco", parametros);
            } catch (Exception e) { return 0; }
        }
    }
}