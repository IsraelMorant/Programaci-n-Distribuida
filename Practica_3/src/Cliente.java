public class Cliente {

    public static void main(String[] args) {
        System.out.println("Iniciando aplicación cliente...");
        
        // Ya no pedimos IP, ni creamos Sockets aquí. 
        // Solo instanciamos el hilo y él se encargará de gritar por Multicast.
        ClienteHilo hilo = new ClienteHilo();
        hilo.start();
    }
}