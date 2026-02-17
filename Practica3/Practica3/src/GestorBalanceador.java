

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import java.net.URL;

public class GestorBalanceador {
    
    // Aquí pondrás las IPs reales de las laptops de tus compañeros que serán los Nodos.
    // Por ahora, simulamos que corren en tu misma máquina pero en diferentes puertos.
    private String[] nodos = {
        "http://127.0.0.1:8081/xmlrpc", // Nodo A
        "http://127.0.0.1:8082/xmlrpc", // Nodo B
        "http://127.0.0.1:8083/xmlrpc"  // Nodo C
    };
    
    // Static para que no se reinicie a 0 en cada petición nueva
    private static int turnoActual = 0; 

    // Este es el método que ejecuta el Cliente por XML-RPC
    public int recibirYDistribuirArchivo(String nombreArchivo) {
        
        int intentos = 0;
        int maxNodos = nodos.length;

        // Bucle para buscar un nodo que NO esté lleno
        while (intentos < maxNodos) {
            String urlNodoDestino = nodos[turnoActual];
            
            // Avanzamos el turno para el próximo archivo (0 -> 1 -> 2 -> 0)
            turnoActual = (turnoActual + 1) % maxNodos;

            System.out.println("[BALANCEADOR] Delegando '" + nombreArchivo + "' a: " + urlNodoDestino);
            
            // Re-enviamos la petición al Nodo elegido
            int respuestaNodo = enviarANodoDestino(urlNodoDestino, nombreArchivo);
            
            if (respuestaNodo == 1 || respuestaNodo == 2) {
                return respuestaNodo; // 1 = Éxito, 2 = Ya existía. Terminamos.
            } else if (respuestaNodo == 3) {
                System.out.println("[BALANCEADOR] Nodo " + urlNodoDestino + " LLENO. Probando siguiente...");
                intentos++;
            } else {
                System.out.println("[BALANCEADOR] Nodo " + urlNodoDestino + " CAÍDO/DESCONECTADO. Probando siguiente...");
                intentos++;
            }
        }
        
        System.out.println("[BALANCEADOR] ERROR CRÍTICO: Todos los nodos están llenos o desconectados.");
        return 3; // Le avisa al cliente que falló la red
    }

    // Funciona como "Cliente" temporal para hablar con los Nodos
    private int enviarANodoDestino(String urlNodo, String nombreArchivo) {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(urlNodo));
            
            XmlRpcClient cliente = new XmlRpcClient();
            cliente.setConfig(config);
            
            Object[] parametros = new Object[]{ nombreArchivo };
            
            // Ejecutamos el método en el Nodo final
            return (Integer) cliente.execute("NodoGestor.guardarEnDisco", parametros);
            
        } catch (Exception e) {
            return 0; // Error de conexión
        }
    }
}