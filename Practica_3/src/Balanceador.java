import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.webserver.WebServer;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public class Balanceador {

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");

        try {
          
            System.out.println("      INICIANDO BALANCEADOR CENTRAL");
     

            new Thread(() -> escucharMulticast()).start();

            WebServer webServer = new WebServer(9000);
            XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
            PropertyHandlerMapping phm = new PropertyHandlerMapping();
            phm.addHandler("Gestor", GestorLogica.class);
            xmlRpcServer.setHandlerMapping(phm);
            webServer.start();

            System.out.println("BB RPC listo en puerto 9000 para recibir archivos.");

        } catch (Exception e) {
            System.err.println("Error fatal: " + e.getMessage());
        }
    }

   private static void escucharMulticast() {
    try {
        // En lugar de new MulticastSocket(10000), usamos esta estructura:
        java.net.InetSocketAddress direccion = new java.net.InetSocketAddress(10000);
        MulticastSocket s = new MulticastSocket(null); // Socket sin puerto aún
        s.setReuseAddress(true);
        s.bind(direccion); // Se amarra al puerto 10000 en todas las IPs

        InetAddress group = InetAddress.getByName("231.0.0.1");
        NetworkInterface ni = getRedFisica();
        if (ni != null) {
            s.setNetworkInterface(ni);
        }

        s.joinGroup(new java.net.InetSocketAddress(group, 0), ni);
        System.out.println("[MULTICAST] Radar activo. Esperando nodos externos...");
        // ... resto del while igual
            while (true) {
                byte[] buffer = new byte[256];
                DatagramPacket dgp = new DatagramPacket(buffer, buffer.length);
                s.receive(dgp); // Recibe el paquete

                String mensaje = new String(dgp.getData(), 0, dgp.getLength());

                if (mensaje.equals("BUSCANDO")) {
                    String respuesta = "AQUI_ESTOY";
                    // IMPORTANTE: Respondemos directamente a la IP y PUERTO del remitente
                    DatagramPacket dgpRespuesta = new DatagramPacket(
                            respuesta.getBytes(), respuesta.length(),
                            dgp.getAddress(), dgp.getPort()
                    );
                    s.send(dgpRespuesta);
                    System.out.println("[MULTICAST] Respondido a: " + dgp.getAddress().getHostAddress());
                }
            }
        } catch (Exception e) {
            System.err.println("[MULTICAST] Error: " + e.getMessage());
        }
    }

    // --- ESCÁNER DE RED FÍSICA ---
    public static NetworkInterface getRedFisica() throws Exception {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets)) {
            String nombre = netint.getDisplayName().toLowerCase();
            if (netint.isUp() && !netint.isLoopback() && netint.supportsMulticast() && 
                !nombre.contains("wsl") && !nombre.contains("virtual") && !nombre.contains("vmware")) {
                for (InetAddress addr : Collections.list(netint.getInetAddresses())) {
                    if (addr instanceof java.net.Inet4Address) return netint;
                }
            }
        }
        return null;
    }

    // --- LÓGICA DE DISTRIBUCIÓN RPC ---
    public static class GestorLogica {
        private static ArrayList<String> nodos = new ArrayList<>();
        private static int turnoActual = 0;

        public boolean registrarNodo(String urlNodo) {
            if (!nodos.contains(urlNodo)) {
                nodos.add(urlNodo);
                System.out.println("R  Nodo añadido: " + urlNodo);
            }
            return true;
        }

        public int recibirYDistribuirArchivo(String nombreArchivo) {
            if (nodos.isEmpty()) return 0;

            int intentos = 0;
            int maxNodos = nodos.size();

            while (intentos < maxNodos) {
                String urlNodoDestino = nodos.get(turnoActual);
                turnoActual = (turnoActual + 1) % maxNodos; 

                System.out.println("EE Mandando '" + nombreArchivo + "' a: " + urlNodoDestino);
                int respuestaNodo = enviarRPC(urlNodoDestino, nombreArchivo);

                if (respuestaNodo == 1 || respuestaNodo == 2) return respuestaNodo; 
                else if (respuestaNodo == 3) {
                    System.out.println(" -> Nodo lleno (Límite 3). Buscando otro...");
                    intentos++;
                } else intentos++;
            }
            return 3; 
        }

        private int enviarRPC(String urlNodo, String nombreArchivo) {
            try {
                XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                config.setServerURL(new URL(urlNodo));
                XmlRpcClient cliente = new XmlRpcClient();
                cliente.setConfig(config);
                Object[] parametros = new Object[]{nombreArchivo};
                return (Integer) cliente.execute("Nodo.guardarEnDisco", parametros);
            } catch (Exception e) { return 0; }
        }
    }
}