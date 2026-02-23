const skeletonify = require('./skeletonify');
const dgram = require('dgram');
const fs = require('fs');
const os = require('os');

<<<<<<< HEAD
const PUERTO = 8081; 

=======
const PUERTO_RPC = 8081;

// Obtenemos la IP real de la computadora
>>>>>>> 35773bbdbc5ee6ce9d4c785e716333d7c94f4d2a
function getIP() {
    const interfaces = os.networkInterfaces();
    let ipRadmin = null, ipWifi = null;
    for (let iface in interfaces) {
        for (let i of interfaces[iface]) {
            if (i.family === 'IPv4' && !i.internal && !iface.toLowerCase().includes('wsl') && !iface.toLowerCase().includes('virtual')) {
                if (iface.toLowerCase().includes('radmin') || i.address.startsWith('172.26.')) ipRadmin = i.address;
                else ipWifi = i.address;
            }
        }
    }
    return ipRadmin || ipWifi || '127.0.0.1';
}

const miIp = getIP();

<<<<<<< HEAD
=======
// --- 1. LÓGICA RPC (Para guardar el archivo cuando el cliente lo mande) ---
>>>>>>> 35773bbdbc5ee6ce9d4c785e716333d7c94f4d2a
const nodoLogica = {
    guardarEnDisco: (nombre) => {
        const dir = './archivos_nodo';
        if (!fs.existsSync(dir)) fs.mkdirSync(dir);
<<<<<<< HEAD

        const archivos = fs.readdirSync(dir);
        if (archivos.length >= 3) return 3; 

        const rutaCompleta = `${dir}/${nombre}`;
        if (fs.existsSync(rutaCompleta)) return 2; 

        fs.writeFileSync(rutaCompleta, "Contenido de prueba");
        console.log(`[NODO] Archivo guardado: ${nombre}`);
        return 1; 
    }
};

skeletonify('Nodo', nodoLogica).listen(PUERTO, () => {
    console.log(`[NODO] Iniciado en ${miUrl}`);
    console.log(`[NODO] Buscando al Balanceador por la VPN...`);
});

// MULTICAST DE BÚSQUEDA FORZADO A LA VPN
const buscador = dgram.createSocket('udp4');
buscador.on('message', async (msg, rinfo) => {
    if (msg.toString() === "AQUI_ESTOY") {
        console.log(`[NODO] ¡Balanceador encontrado en ${rinfo.address}!`);
        buscador.close();
        
        const balanceadorRemoto = stubify(`http://${rinfo.address}:9000`, 'Gestor', ['registrarNodo']);
        await balanceadorRemoto.registrarNodo(miUrl);
        console.log("[NODO] ¡Registrado con éxito y listo para recibir archivos!");
    }
});

buscador.bind(() => {
    buscador.addMembership('231.0.0.1', miIp); // Escuchar respuestas por VPN
    buscador.setMulticastInterface(miIp);      // Gritar por VPN
    buscador.send(Buffer.from("BUSCANDO"), 10000, '231.0.0.1');
=======
        
        const rutaCompleta = `${dir}/${nombre}`;
        fs.writeFileSync(rutaCompleta, "Contenido descentralizado");
        console.log(`[NODO] Archivo guardado exitosamente: ${nombre}`);
        return 1; // Éxito
    }
};

skeletonify('Nodo', nodoLogica).listen(PUERTO_RPC, '0.0.0.0', () => {
    console.log(`[NODO P2P] Servidor RPC abierto en http://${miIp}:${PUERTO_RPC}`);
});

// --- 2. LÓGICA DE DESCUBRIMIENTO DESCENTRALIZADO (UDP) ---
const radar = dgram.createSocket('udp4');

radar.on('message', (msg, rinfo) => {
    const mensaje = msg.toString();
    
    // Si un cliente está buscando espacio
    if (mensaje.startsWith("QUIEN_TIENE_ESPACIO:")) {
        const nombreArchivo = mensaje.split(":")[1];
        
        const dir = './archivos_nodo';
        if (!fs.existsSync(dir)) fs.mkdirSync(dir);
        const archivos = fs.readdirSync(dir);
        
        // Verificamos si tenemos capacidad (<3) y si no tenemos ya ese archivo
        if (archivos.length < 3 && !fs.existsSync(`${dir}/${nombreArchivo}`)) {
            // Le respondemos directo al cliente que gritó
            const respuesta = Buffer.from(`TENGO_ESPACIO:${miIp}:${PUERTO_RPC}`);
            radar.send(respuesta, rinfo.port, rinfo.address, () => {
                console.log(`[P2P] Ofrecí espacio a ${rinfo.address} para el archivo '${nombreArchivo}'`);
            });
        } else if (archivos.length >= 3) {
            console.log(`[P2P] Ignorando petición de '${nombreArchivo}'. Mi disco está LLENO.`);
        } else {
            console.log(`[P2P] Ignorando petición. Ya tengo una copia de '${nombreArchivo}'.`);
        }
    }
});

// Escuchamos los gritos de la red en el puerto 10000
radar.bind(10000, '0.0.0.0', () => {
    radar.setBroadcast(true);
    console.log(`[P2P] Radar activo: Escuchando peticiones de clientes...`);
>>>>>>> 35773bbdbc5ee6ce9d4c785e716333d7c94f4d2a
});