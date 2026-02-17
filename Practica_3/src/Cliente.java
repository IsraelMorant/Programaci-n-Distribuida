public class Cliente {
    public static void main(String[] args) {
        // Forzamos IPv4 para evitar que Windows bloquee el Multicast
        System.setProperty("java.net.preferIPv4Stack", "true"); 
        
        System.out.println("Iniciando aplicación cliente...");
        ClienteHilo hilo = new ClienteHilo();
        hilo.start();
    }
}