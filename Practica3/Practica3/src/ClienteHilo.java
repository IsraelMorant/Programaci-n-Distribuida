import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URL;
import javax.swing.*;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class ClienteHilo extends Thread {

    private static final String RUTA_LOCAL = "./archivos_cliente"; // Carpeta local del cliente
    private static final int LIMITE_ARCHIVOS = 3; 
    
    // Aquí guardaremos la IP del balanceador una vez que lo descubramos
    private String ipBalanceadorDescubierto = null;

    public void run() {
        // 1. ANTES de mostrar la ventana, buscamos al Balanceador en la red
        ipBalanceadorDescubierto = descubrirBalanceador();

        // 2. Creando las ventanas del programa (Tu mismo diseño)
        JFrame ventanaInicial = new JFrame();
        ventanaInicial.setSize(1200, 800);
        ventanaInicial.setTitle("Menu Principal - Sistema Distribuido");
        ventanaInicial.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventanaInicial.setLocationRelativeTo(null);

        JPanel panelInicial = new JPanel();
        panelInicial.setLayout(null);
        panelInicial.setBackground(Color.WHITE);

        JTextField txtNombre = new JTextField(20);
        txtNombre.setBounds(500, 500, 150, 50);

        JLabel lblBienvenido = new JLabel("Balanceador en: " + 
                (ipBalanceadorDescubierto != null ? ipBalanceadorDescubierto : "NO ENCONTRADO"));
        lblBienvenido.setFont(new Font("arial", Font.ITALIC, 15));
        lblBienvenido.setHorizontalAlignment(JLabel.CENTER);
        lblBienvenido.setBounds(350, 0, 500, 100);
        lblBienvenido.setForeground(Color.black);

        JButton btnCrear = new JButton("Crear Archivo");
        btnCrear.setFont(new Font("arial", Font.ITALIC, 19));
        btnCrear.setBounds(400, 400, 350, 50);
        
        // ACCIÓN DEL BOTÓN
        btnCrear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new File(RUTA_LOCAL).mkdirs();
                String nombreArchivo = txtNombre.getText();
                
                if (nombreArchivo.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Ingrese un nombre", "Alerta", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int cantidadActual = contarArchivosLocales();
                System.out.println("[INFO CLIENTE] Archivos locales actuales: " + cantidadActual);
                
                // --- LÓGICA TRANSPARENTE ---
                if (cantidadActual < LIMITE_ARCHIVOS) {
                    // Guarda localmente primero (Tu requerimiento original)
                    int opc = crearLocalmente(nombreArchivo);
                    procesarRespuesta(opc);
                } else {
                    // Límite local alcanzado -> Se delega al Sistema Distribuido
                    if (ipBalanceadorDescubierto == null) {
                        JOptionPane.showMessageDialog(null, "Límite local alcanzado y no hay balanceador en red.", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        System.out.println("[CLIENTE] Límite alcanzado. Enviando por RPC...");
                        int opc = enviarPeticionRPC(nombreArchivo);
                        procesarRespuesta(opc);
                    }
                }
            }
        });

        panelInicial.add(lblBienvenido);
        panelInicial.add(btnCrear);
        panelInicial.add(txtNombre);
        ventanaInicial.add(panelInicial);
        ventanaInicial.setVisible(true);
    }

    /**
     * =========================================
     * MÉTODOS DE RED (Multicast y RPC)
     * =========================================
     */
     
    // Grita en la red local buscando al Balanceador
    private String descubrirBalanceador() {
        try {
            System.out.println("[CLIENTE] Buscando balanceador por Multicast...");
            InetAddress grupo = InetAddress.getByName("230.0.0.1");
            MulticastSocket socket = new MulticastSocket();
            
            String mensaje = "BUSCANDO_BALANCEADOR";
            DatagramPacket paqueteSalida = new DatagramPacket(mensaje.getBytes(), mensaje.length(), grupo, 4446);
            socket.send(paqueteSalida); // Enviamos el grito
            
            // Esperamos respuesta máximo 3 segundos para no congelar la ventana
            socket.setSoTimeout(3000); 
            byte[] buffer = new byte[256];
            DatagramPacket paqueteEntrada = new DatagramPacket(buffer, buffer.length);
            socket.receive(paqueteEntrada); // Recibimos la respuesta
            
            socket.close();
            String ipEncontrada = paqueteEntrada.getAddress().getHostAddress();
            System.out.println("[CLIENTE] ¡Balanceador encontrado en " + ipEncontrada + "!");
            return ipEncontrada;
            
        } catch (Exception e) {
            System.out.println("[CLIENTE] No se encontró balanceador en la red local.");
            return null;
        }
    }

    // Se conecta al balanceador descubierto y ejecuta el método remoto
    private int enviarPeticionRPC(String nombreArchivo) {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            // Nos conectamos al puerto 8080 del balanceador descubierto
            config.setServerURL(new URL("http://" + ipBalanceadorDescubierto + ":8080/xmlrpc"));
            
            XmlRpcClient cliente = new XmlRpcClient();
            cliente.setConfig(config);
            
            // Empaquetamos el parámetro (solo el nombre del archivo)
            Object[] parametros = new Object[]{ nombreArchivo };
            
            // Ejecutamos la función en el Balanceador
            Integer respuesta = (Integer) cliente.execute("GestorBalanceador.recibirYDistribuirArchivo", parametros);
            
            return respuesta;
        } catch (Exception e) {
            System.err.println("[CLIENTE] Error de conexión RPC: " + e.getMessage());
            return 0; // 0 = Error
        }
    }

    /**
     * =========================================
     * MÉTODOS DE UTILIDAD ORIGINALES
     * =========================================
     */
    private void procesarRespuesta(int codigo) {
        switch (codigo) {
            case 1:
                JOptionPane.showMessageDialog(null, "Archivo creado con éxito", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                break;
            case 2:
                JOptionPane.showMessageDialog(null, "El archivo ya existe", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                break;
            case 3:
                JOptionPane.showMessageDialog(null, "Error: Todos los nodos del sistema están llenos", "Capacidad Máxima", JOptionPane.ERROR_MESSAGE);
                break;
            case 0:
            default:
                JOptionPane.showMessageDialog(null, "Error al procesar el archivo", "Aviso", JOptionPane.WARNING_MESSAGE);
                break;
        }
    }

    private static int contarArchivosLocales() {
        File carpeta = new File(RUTA_LOCAL);
        File[] archivos = carpeta.listFiles();
        int contador = 0;
        if (archivos != null) {
            for (File f : archivos) {
                if (f.isFile()) contador++;
            }
        }
        return contador;
    }

    private static int crearLocalmente(String nombre) {
        File archivo = new File(RUTA_LOCAL, nombre);
        try {
            if (archivo.createNewFile()) {
                System.out.println("[LOCAL] Archivo creado: " + nombre);
                return 1;
            } else {
                return 2;
            }
        } catch (IOException e) {
            return 0;
        }
    }
}