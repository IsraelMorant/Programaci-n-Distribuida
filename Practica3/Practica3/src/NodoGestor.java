

import java.io.File;
import java.io.IOException;

public class NodoGestor {
    
    // Cambiaremos ligeramente la ruta para las pruebas locales
    private static final String RUTA_LOCAL = "./archivos_nodo"; 
    private static final int LIMITE_ARCHIVOS = 3; 

    // Este es el método que el Balanceador ejecutará por RPC
    public int guardarEnDisco(String nombre) {
        
        // 1. Verificamos el límite
        int cantidadActual = contarArchivosLocales();
        System.out.println("[INFO NODO] Archivos actuales: " + cantidadActual + " / " + LIMITE_ARCHIVOS);
        
        if (cantidadActual >= LIMITE_ARCHIVOS) {
            System.out.println("[NODO] ¡Capacidad máxima alcanzada! Rechazando petición.");
            return 3; // Código 3: Lleno
        }
        
        // 2. Si hay espacio, procedemos a crear
        File carpeta = new File(RUTA_LOCAL);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
        
        File archivo = new File(RUTA_LOCAL, nombre);
        try {
            if (archivo.createNewFile()) {
                System.out.println("[NODO] Archivo creado exitosamente: " + nombre);
                return 1; // Código 1: Éxito
            } else {
                System.out.println("[NODO] El archivo ya existía.");
                return 2; // Código 2: Ya existe
            }
        } catch (IOException e) {
            System.err.println("[NODO] Error de I/O: " + e.getMessage());
            return 0; // Código 0: Error general
        }
    }

    private int contarArchivosLocales() {
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
}