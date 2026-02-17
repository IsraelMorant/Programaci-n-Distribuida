

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.webserver.WebServer;

public class ServidorBalanceadorMain {

    public static void main(String[] args) {
        try {
            System.out.println("Iniciando Sistema Distribuido...");
            
            // 1. Levantamos el radar de Multicast en un hilo paralelo
            HiloMulticastBalanceador hiloMulticast = new HiloMulticastBalanceador();
            hiloMulticast.start();

            // 2. Levantamos el servidor XML-RPC en el puerto 8080
            WebServer webServer = new WebServer(9000);
            XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();

            // 3. Le decimos al servidor qué clase tiene permiso de ser ejecutada por los clientes
            PropertyHandlerMapping phm = new PropertyHandlerMapping();
            
            // Exponemos la clase GestorBalanceador bajo el nombre "GestorBalanceador"
            phm.addHandler("GestorBalanceador", GestorBalanceador.class);
            xmlRpcServer.setHandlerMapping(phm);

            // 4. Arrancamos el servidor web
            webServer.start();
            System.out.println("[SERVIDOR] XML-RPC listo y escuchando en el puerto 8080.");
            
        } catch (Exception e) {
            System.err.println("Error fatal al iniciar el servidor: " + e.getMessage());
        }
    }
}