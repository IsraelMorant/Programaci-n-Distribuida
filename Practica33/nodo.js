const skeletonify = require('./skeletonify');
const dgram = require('dgram');
const fs = require('fs');
const os = require('os');

const PUERTO_RPC = 8081;

// Obtenemos la IP real de la computadora
function getIP() {
    const interfaces = os.networkInterfaces();
    for (let iface in interfaces) {
        for (let i of interfaces[iface]) {
            if (i.family === 'IPv4' && !i.internal && !iface.toLowerCase().includes('wsl') && !iface.toLowerCase().includes('virtual')) {
                return i.address;
            }
        }
    }
    return '127.0.0.1';
}

const miIp = getIP();


const nodoLogica = {
    guardarEnDisco: (nombre) => {
        const dir = './archivos_nodo';
        if (!fs.existsSync(dir)) fs.mkdirSync(dir);
        
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
});