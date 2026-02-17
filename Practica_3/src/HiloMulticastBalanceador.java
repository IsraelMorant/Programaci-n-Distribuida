

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class HiloMulticastBalanceador extends Thread {
    
    @Override
    public void run() {
        try {
            // Se une al grupo Multicast
            MulticastSocket socket = new MulticastSocket(4444);
            InetAddress grupo = InetAddress.getByName("230.0.0.1");
            socket.joinGroup(grupo);
            
            System.out.println("[MULTICAST] Escuchando en la red local (230.0.0.1:4446)...");
            
            while (true) {
                byte[] buffer = new byte[256];
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                
                // El hilo se pausa aquí hasta que un Cliente manda su mensaje
                socket.receive(paquete);
                
                String mensaje = new String(paquete.getData(), 0, paquete.getLength());
                
                // Si el mensaje es el esperado, le contestamos
                if (mensaje.equals("BUSCANDO_BALANCEADOR")) {
                    System.out.println("[MULTICAST] Cliente detectado en IP: " + paquete.getAddress().getHostAddress());
                    
                    String respuesta = "AQUI_ESTOY";
                    DatagramPacket respuestaPaquete = new DatagramPacket(
                        respuesta.getBytes(), respuesta.length(), 
                        paquete.getAddress(), paquete.getPort()
                    );
                    socket.send(respuestaPaquete); // Le mandamos la respuesta directa
                }
            }
        } catch (Exception e) {
            System.err.println("[MULTICAST] Error: " + e.getMessage());
        }
    }
}