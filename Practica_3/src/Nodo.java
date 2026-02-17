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
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;

public class Nodo {

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        int puerto = 8081; 

        try {
            WebServer webServer = new WebServer(puerto);
            XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
            PropertyHandlerMapping phm = new PropertyHandlerMapping();
            phm.addHandler("Nodo", Nodo.class);
            xmlRpcServer.setHandlerMapping(phm);
            webServer.start();

            // PARCHE: Obtenemos la IP real de la red, no la de WSL
            String miIp = getIpReal();
            String miUrl = "http://" + miIp + ":" + puerto + "/xmlrpc";

         
            System.out.println("NN Iniciado en: " + miUrl);
            System.out.println("NN Buscando al Balanceador");

            String ipBalanceador = descubrirBalanceador();

            if (ipBalanceador != null) {
                System.out.println("NN Balanceador encontrado en " + ipBalanceador);
                registrarEnBalanceador(ipBalanceador, miUrl);
            } else {
                System.out.println("NN ERROR: No se encontró Balanceador.");
            }
           

        } catch (Exception e) {
            System.err.println("Error fatal: " + e.getMessage());
        }
    }

    private static String descubrirBalanceador() {
        try {
            MulticastSocket s = new MulticastSocket();
            InetAddress group = InetAddress.getByName("231.0.0.1");
            
            
            NetworkInterface ni = getRedFisica();
            if (ni != null) s.setNetworkInterface(ni);
            s.setTimeToLive(5);

            byte[] msj = "buscando".getBytes();
            DatagramPacket dgp = new DatagramPacket(msj, msj.length, group, 10000);
            s.setTimeToLive(255); // Alcance máximo en la red local
            s.send(dgp);

            s.setSoTimeout(5000);
            byte[] buffer = new byte[256];
            DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length);
            s.receive(respuesta);
            
            String ipEncontrada = respuesta.getAddress().getHostAddress();
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
            System.out.println("NN Registrado con exito");
        } catch (Exception e) { System.err.println("Error al registrar: " + e.getMessage()); }
    }

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

    public static String getIpReal() throws Exception {
        NetworkInterface netint = getRedFisica();
        if (netint != null) {
            for (InetAddress addr : Collections.list(netint.getInetAddresses())) {
                if (addr instanceof java.net.Inet4Address) return addr.getHostAddress();
            }
        }
        return InetAddress.getLocalHost().getHostAddress(); 
    }

   
    public int guardarEnDisco(String nombre) {
        String carpeta = "./archivos_nodo";
        File dir = new File(carpeta);
        dir.mkdirs();

        int cont = 0;
        if (dir.listFiles() != null) {
            for (File f : dir.listFiles()) { if (f.isFile()) cont++; }
        }

        if (cont >= 3) return 3; 

        try {
            if (new File(carpeta, nombre).createNewFile()) {
                System.out.println("NN Archivo guardado: " + nombre);
                return 1;
            } else return 2;
        } catch (IOException e) { return 0; }
    }
}