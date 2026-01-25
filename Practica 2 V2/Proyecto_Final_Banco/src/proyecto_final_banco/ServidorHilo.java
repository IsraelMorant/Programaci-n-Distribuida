/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto_final_banco;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author israe
 */




public class ServidorHilo extends Thread {

     private static final String RUTA_LOCAL = "./archivos";

    private DataInputStream in;
    private DataOutputStream out ;
    private String numeroCliente ;
    
    public ServidorHilo(DataInputStream in, DataOutputStream out, String numeroCliente) {
        this.in = in;
        this.out = out;
        this.numeroCliente = numeroCliente;
    }
    
    
 
    
    public void run(){
        Random  r = new Random(System.currentTimeMillis());
        int opcion;
        String mensaje;
        while(true){
        
            try {
                opcion = in.readInt();
                
                switch(opcion){
                    
                    case 1://Para dar de alta en la base de datos al usuario
                   
                        break;
                    case 2:
                   
                        break;
                    case 3://Cuando el cliente pida validacion para ingresar al menu de usuario con sus credenciales
                          
                
                            
                        break;
                    case 4://En este caso es para cuando oprima la opcion de depositar a su cuenta
                         
                       
                        break;
                    case 5://Este caso es para cuando el usuario quiera agregar contactos
                        
                        
                        
                  
                        break;
                    case 6://Este caso es para cuando el usuario quiera dar de baja a uno de sus contactos dado el numero de cuenta de su contacto
                        
                             
                        break;
                    case 7://El caso para cuando el usuario quiera depositar/pagar a uno de sus contactos
                        
                        
                        break;
                    case 8:
                       
                        break;
                    case 33://Opcion para crear archivo en el servidor
                              // 1. Verificamos cuántos archivos tenemos
                int cantidadActual = contarArchivosLocales();
                System.out.println("[INFO] Archivos locales/Server actuales: " + cantidadActual);
                 String nombreArchivo = in.readUTF();
               
                    // CASO A: Crear Localmente
                    int opc = crearLocalmente(nombreArchivo);

                    switch (opc) {
                        case 1:
                           // JOptionPane.showMessageDialog(null, "Archivo creado con exito","Aviso", JOptionPane.INFORMATION_MESSAGE);
                           out.writeInt(1);
                            break;
                        case 2:
                            //JOptionPane.showMessageDialog(null, "El archivo ya existe","Aviso", JOptionPane.INFORMATION_MESSAGE);
                            out.writeInt(2);
                            break;

                        case 0:
                            //JOptionPane.showMessageDialog(null, "Error al crear archivo","Aviso", JOptionPane.WARNING_MESSAGE);
                            out.writeInt(0);
                            break;
                        default:
                            throw new AssertionError();
                    }

                        break;
                    case 10:
                        break;
                    case 11:
                        break;
                        
                    default:
                       out.writeUTF("Escoje una opcion validad");
                        
                }
            } catch (IOException ex) {
                Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
            }
           
        
        
        
        }
        
    }


    //Funciones para crear y contar archivos localmente
     // Cuenta archivos reales en la carpeta local
    private static int contarArchivosLocales() {
        File carpeta = new File(RUTA_LOCAL);
        File[] archivos = carpeta.listFiles();
        int contador = 0;
        if (archivos != null) {
            for (File f : archivos) {
                if (f.isFile()) contador++; // Solo contamos archivos, no carpetas
            }
        }
        return contador;
    }

    private static int  crearLocalmente(String nombre) {
        File archivo = new File(RUTA_LOCAL, nombre);
        try {
            if (archivo.createNewFile()) {
                System.out.println("[LOCAL] Archivo creado: " + nombre);
                return 1;
            } else {
                System.out.println("[LOCAL] El archivo ya existe.");
                return 2;
            }
        } catch (IOException e) {
            System.err.println("[LOCAL] Error creando archivo: " + e.getMessage());
            return 0;
        }
    }
}
