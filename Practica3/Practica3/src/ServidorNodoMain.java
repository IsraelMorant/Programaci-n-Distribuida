

import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.webserver.WebServer;

public class ServidorNodoMain {

    public static void main(String[] args) {
        // En tu GestorBalanceador definimos los puertos 8081, 8082 y 8083.
        // Si pruebas todo en tu PC, cambia este número y ejecuta el archivo 3 veces.
        int puerto = 8081; 

        try {
            // 1. Creamos el servidor Web en el puerto asignado
            WebServer webServer = new WebServer(puerto);
            XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();

            // 2. Mapeamos la clase que permitimos ejecutar
            PropertyHandlerMapping phm = new PropertyHandlerMapping();
            phm.addHandler("NodoGestor", NodoGestor.class);
            xmlRpcServer.setHandlerMapping(phm);

            // 3. ¡Arrancamos!
            webServer.start();
            System.out.println("=========================================");
            System.out.println("[NODO] Servidor iniciado en el puerto " + puerto);
            System.out.println("[NODO] Esperando instrucciones del Balanceador...");
            System.out.println("=========================================");
            
        } catch (Exception e) {
            System.err.println("[NODO] Error al iniciar: " + e.getMessage());
        }
    }
}