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

    private static final String RUTA_LOCAL = "./archivos_cliente";
    private static final int LIMITE_ARCHIVOS = 3; 
    private String ipBalanceadorDescubierto = null;

    public void run() {
        ipBalanceadorDescubierto = descubrirBalanceador();

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
                
                if (cantidadActual < LIMITE_ARCHIVOS) {
                    int opc = crearLocalmente(nombreArchivo);
                    procesarRespuesta(opc);
                } else {
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

    private String descubrirBalanceador() {
        try {
            System.out.println("[CLIENTE] Buscando balanceador por Multicast...");
            InetAddress grupo = InetAddress.getByName("230.0.0.1");
            MulticastSocket socket = new MulticastSocket();
            
            // Ajustes extremos para redes Wi-Fi externas
            socket.setInterface(InetAddress.getLocalHost());
            socket.setTimeToLive(5); 
            
            String mensaje = "BUSCANDO_BALANCEADOR";
            DatagramPacket paqueteSalida = new DatagramPacket(mensaje.getBytes(), mensaje.length(), grupo, 4446);
            socket.send(paqueteSalida); 
            
            socket.setSoTimeout(3000); 
            byte[] buffer = new byte[256];
            DatagramPacket paqueteEntrada = new DatagramPacket(buffer, buffer.length);
            socket.receive(paqueteEntrada); 
            
            socket.close();
            return paqueteEntrada.getAddress().getHostAddress();
        } catch (Exception e) {
            System.out.println("[CLIENTE] No se encontró balanceador.");
            return null;
        }
    }

    private int enviarPeticionRPC(String nombreArchivo) {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL("http://" + ipBalanceadorDescubierto + ":9000/xmlrpc"));
            XmlRpcClient cliente = new XmlRpcClient();
            cliente.setConfig(config);
            
            Object[] parametros = new Object[]{ nombreArchivo };
            return (Integer) cliente.execute("GestorBalanceador.recibirYDistribuirArchivo", parametros);
        } catch (Exception e) {
            return 0; 
        }
    }

    private void procesarRespuesta(int codigo) {
        switch (codigo) {
            case 1: JOptionPane.showMessageDialog(null, "Archivo creado con éxito", "Aviso", JOptionPane.INFORMATION_MESSAGE); break;
            case 2: JOptionPane.showMessageDialog(null, "El archivo ya existe", "Aviso", JOptionPane.INFORMATION_MESSAGE); break;
            case 3: JOptionPane.showMessageDialog(null, "Error: Todos los nodos están llenos", "Capacidad Máxima", JOptionPane.ERROR_MESSAGE); break;
            default: JOptionPane.showMessageDialog(null, "Error al procesar el archivo", "Aviso", JOptionPane.WARNING_MESSAGE); break;
        }
    }

    private static int contarArchivosLocales() {
        File carpeta = new File(RUTA_LOCAL);
        File[] archivos = carpeta.listFiles();
        int contador = 0;
        if (archivos != null) {
            for (File f : archivos) { if (f.isFile()) contador++; }
        }
        return contador;
    }

    private static int crearLocalmente(String nombre) {
        File archivo = new File(RUTA_LOCAL, nombre);
        try {
            if (archivo.createNewFile()) { return 1; } 
            else { return 2; }
        } catch (IOException e) { return 0; }
    }
}