import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import java.net.URL;
import java.util.ArrayList;

public class GestorBalanceador {
    
    // ¡Adiós a las IPs quemadas! Ahora es una lista dinámica
    private static ArrayList<String> nodos = new ArrayList<>();
    private static int turnoActual = 0; 

    // NUEVO MÉTODO: Los nodos llamarán a este método para anunciarse
    public boolean registrarNodo(String urlNodo) {
        if (!nodos.contains(urlNodo)) {
            nodos.add(urlNodo);
            System.out.println("[BALANCEADOR] ¡Nuevo NODO unido a la red!: " + urlNodo);
            System.out.println("[BALANCEADOR] Total de nodos disponibles: " + nodos.size());
        }
        return true;
    }

    // El método que ya teníamos, adaptado a la lista dinámica
    public int recibirYDistribuirArchivo(String nombreArchivo) {
        if (nodos.isEmpty()) {
            System.out.println("[BALANCEADOR] ERROR: No hay ningún nodo conectado a la red para guardar el archivo.");
            return 0; 
        }

        int intentos = 0;
        int maxNodos = nodos.size(); // Usamos el tamaño de la lista

        while (intentos < maxNodos) {
            String urlNodoDestino = nodos.get(turnoActual);
            
            // Avanzamos el turno
            turnoActual = (turnoActual + 1) % maxNodos;

            System.out.println("[BALANCEADOR] Delegando '" + nombreArchivo + "' a: " + urlNodoDestino);
            int respuestaNodo = enviarANodoDestino(urlNodoDestino, nombreArchivo);
            
            if (respuestaNodo == 1 || respuestaNodo == 2) {
                return respuestaNodo; 
            } else if (respuestaNodo == 3) {
                System.out.println("[BALANCEADOR] Nodo " + urlNodoDestino + " LLENO. Probando siguiente...");
                intentos++;
            } else {
                System.out.println("[BALANCEADOR] Nodo " + urlNodoDestino + " CAÍDO. Probando siguiente...");
                intentos++;
            }
        }
        
        System.out.println("[BALANCEADOR] ERROR CRÍTICO: Todos los nodos están llenos o desconectados.");
        return 3; 
    }

    private int enviarANodoDestino(String urlNodo, String nombreArchivo) {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(urlNodo));
            XmlRpcClient cliente = new XmlRpcClient();
            cliente.setConfig(config);
            
            Object[] parametros = new Object[]{ nombreArchivo };
            return (Integer) cliente.execute("NodoGestor.guardarEnDisco", parametros);
        } catch (Exception e) {
            return 0; 
        }
    }
}