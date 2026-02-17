import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import javax.swing.*;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class Cliente {

    private static String ipBalanceador = null;

    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.out.println("CC Buscando Balanceador");
        ipBalanceador = descubrirBalanceador();
        crearVentana();
    }

    private static String descubrirBalanceador() {
        try {
            MulticastSocket s = new MulticastSocket();
            InetAddress group = InetAddress.getByName("231.0.0.1");
            
            
            NetworkInterface ni = getRedFisica();
            if (ni != null) s.setNetworkInterface(ni);
            s.setTimeToLive(5); 

            byte[] msj = "BUSCANDO".getBytes();
            DatagramPacket dgp = new DatagramPacket(msj, msj.length, group, 10000);
            s.send(dgp);

            s.setSoTimeout(3000);
            byte[] buffer = new byte[256];
            DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length);
            s.receive(respuesta);
            
            String ip = respuesta.getAddress().getHostAddress();
            s.close();
            System.out.println(" CC Balanceador encontrado en: " + ip);
            return ip;
        } catch (Exception e) {
            System.out.println("CC No se encontró Balanceador en la red.");
            return null;
        }
    }

    // --- ESCÁNER DE RED FÍSICA ---
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

    private static void crearVentana() {
        JFrame ventana = new JFrame("Menu Principal - Sistema Distribuido");
        ventana.setSize(1200, 800);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setLocationRelativeTo(null);

        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);

        JTextField txtNombre = new JTextField(20);
        txtNombre.setBounds(500, 500, 150, 50);

        String textoLabel = (ipBalanceador != null) ? "Balanceador en: " + ipBalanceador : "BALANCEADOR NO ENCONTRADO";
        JLabel lblBienvenido = new JLabel(textoLabel);
        lblBienvenido.setFont(new Font("arial", Font.ITALIC, 15));
        lblBienvenido.setHorizontalAlignment(JLabel.CENTER);
        lblBienvenido.setBounds(350, 0, 500, 100);

        JButton btnCrear = new JButton("Crear Archivo");
        btnCrear.setFont(new Font("arial", Font.ITALIC, 19));
        btnCrear.setBounds(400, 400, 350, 50);

        btnCrear.addActionListener(e -> {
            String nombre = txtNombre.getText();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese un nombre");
                return;
            }

            File dir = new File("./archivos_cliente");
            dir.mkdirs();
            
            int cont = 0;
            if (dir.listFiles() != null) {
                for (File f : dir.listFiles()) { if (f.isFile()) cont++; }
            }

            if (cont < 3) {
                try {
                    if (new File(dir, nombre).createNewFile()) {
                        JOptionPane.showMessageDialog(null, "Archivo creado localmente con éxito");
                    } else {
                        JOptionPane.showMessageDialog(null, "El archivo ya existe");
                    }
                } catch (IOException ex) { JOptionPane.showMessageDialog(null, "Error al crear"); }
            } else {
                if (ipBalanceador == null) {
                    JOptionPane.showMessageDialog(null, "Límite local alcanzado y no hay red.");
                } else {
                    int resp = enviarRPC(nombre);
                    if (resp == 1) JOptionPane.showMessageDialog(null, "Archivo creado en la red");
                    else if (resp == 2) JOptionPane.showMessageDialog(null, "El archivo ya existe en la red");
                    else if (resp == 3) JOptionPane.showMessageDialog(null, "ERROR: La red está llena (Límite máximo)");
                    else JOptionPane.showMessageDialog(null, "Error de conexión");
                }
            }
        });

        panel.add(lblBienvenido);
        panel.add(btnCrear);
        panel.add(txtNombre);
        ventana.add(panel);
        ventana.setVisible(true);
    }

    private static int enviarRPC(String nombreArchivo) {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL("http://" + ipBalanceador + ":9000/xmlrpc"));
            XmlRpcClient cliente = new XmlRpcClient();
            cliente.setConfig(config);
            Object[] parametros = new Object[]{nombreArchivo};
            return (Integer) cliente.execute("Gestor.recibirYDistribuirArchivo", parametros);
        } catch (Exception e) { return 0; }
    }
}