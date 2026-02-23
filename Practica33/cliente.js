const http = require('http');
const fs = require('fs');
const path = require('path');
const dgram = require('dgram');
const os = require('os');
const stubify = require('./stubify');

<<<<<<< HEAD
const PUERTO_WEB = 3000; 
let ipBalanceador = null;
let balanceadorRPC = null;

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

const server = http.createServer(async (req, res) => {
    if (req.method === 'GET' && req.url === '/') {
        fs.readFile(path.join(__dirname, 'index.html'), (err, data) => {
            if (err) { res.writeHead(500); res.end('Error al cargar index.html'); return; }
=======
const PUERTO_WEB = 3000;

// --- SERVIDOR WEB LOCAL (No cambia, es la misma interfaz) ---
const server = http.createServer(async (req, res) => {
    if (req.method === 'GET' && req.url === '/') {
        fs.readFile(path.join(__dirname, 'index.html'), (err, data) => {
            if (err) { res.writeHead(500); res.end('Error'); return; }
>>>>>>> 35773bbdbc5ee6ce9d4c785e716333d7c94f4d2a
            res.writeHead(200, { 'Content-Type': 'text/html' });
            res.end(data);
        });
    } else if (req.method === 'POST' && req.url === '/api/crear') {
        let body = '';
        req.on('data', chunk => { body += chunk.toString(); });
        req.on('end', async () => {
            const { nombreArchivo } = JSON.parse(body);
            const resultado = await procesarCreacionArchivo(nombreArchivo);
            res.writeHead(200, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify(resultado));
        });
    } else {
        res.writeHead(404); res.end('No encontrado');
    }
});

async function procesarCreacionArchivo(nombre) {
<<<<<<< HEAD
    if (!ipBalanceador) return { tipo: 'error', mensaje: "Aún buscando Balanceador en la red..." };

=======
>>>>>>> 35773bbdbc5ee6ce9d4c785e716333d7c94f4d2a
    const dirLocal = './archivos_cliente';
    if (!fs.existsSync(dirLocal)) fs.mkdirSync(dirLocal);
    const archivosLocales = fs.readdirSync(dirLocal);
    const rutaCompleta = `${dirLocal}/${nombre}`;

    // INTENTO LOCAL
    if (archivosLocales.length < 3) {
        if (fs.existsSync(rutaCompleta)) return { tipo: 'info', mensaje: "El archivo ya existe localmente." };
<<<<<<< HEAD
        fs.writeFileSync(rutaCompleta, "Contenido web");
        return { tipo: 'exito', mensaje: "Archivo creado localmente con éxito." };
    } else {
        console.log(`[WEB] Límite local lleno. Enviando '${nombre}' a la red...`);
        try {
            const res = await balanceadorRPC.recibirYDistribuirArchivo(nombre);
            if (res === 1) return { tipo: 'exito', mensaje: "Límite local lleno. Archivo guardado en la RED distribuida." };
            if (res === 2) return { tipo: 'info', mensaje: "El archivo ya existe en la RED." };
            if (res === 3) return { tipo: 'error', mensaje: "ERROR: Capacidad máxima de la red alcanzada." };
            return { tipo: 'error', mensaje: "Error desconocido en la red." };
        } catch (e) { return { tipo: 'error', mensaje: "Fallo al conectar con el Balanceador." }; }
    }
}

console.log(`[CLIENTE WEB] Iniciando en VPN ${miIp}... Buscando Balanceador.`);
const buscador = dgram.createSocket('udp4');

buscador.on('message', (msg, rinfo) => {
    if (msg.toString() === "AQUI_ESTOY") {
        ipBalanceador = rinfo.address;
        console.log(`[CLIENTE WEB] ¡Balanceador encontrado en ${ipBalanceador}!`);
        balanceadorRPC = stubify(`http://${ipBalanceador}:9000`, 'Gestor', ['recibirYDistribuirArchivo']);
        buscador.close();

        server.listen(PUERTO_WEB, () => {
<<<<<<< HEAD
            console.log(`\n==================================================`);
            console.log(`   ✅ INTERFAZ WEB LISTA`);
            console.log(`   👉 Abre en tu navegador: http://localhost:${PUERTO_WEB}`);
            console.log(`==================================================\n`);
=======
            
            console.log(`   Interfaz`);
            console.log(`    http://localhost:${PUERTO_WEB}`);
            
>>>>>>> b737776f5bbf52d539f772a2f6d0623623a20fe8
=======
        fs.writeFileSync(rutaCompleta, "Contenido creado desde Web");
        return { tipo: 'exito', mensaje: "Archivo creado localmente con éxito." };
    } else {
        // INTENTO DESCENTRALIZADO
        console.log(`\n[WEB] Límite local alcanzado. Buscando nodo disponible para '${nombre}'...`);
        return await buscarNodoYEnviar(nombre);
    }
}

// --- LÓGICA P2P (El corazón del sistema distribuido) ---
function buscarNodoYEnviar(nombreArchivo) {
    return new Promise((resolve) => {
        const clienteUDP = dgram.createSocket('udp4');
        let resuelto = false;

        // Temporizador: Si nadie responde en 2 segundos, la red está llena o apagada
        const timeout = setTimeout(() => {
            if (!resuelto) {
                resuelto = true;
                clienteUDP.close();
                console.log(`[P2P] Nadie respondió. Red llena.`);
                resolve({ tipo: 'error', mensaje: "[ERROR] Capacidad máxima. Ningún nodo en la red tiene espacio." });
            }
        }, 2000);

        // Escuchamos a ver si algún Nodo nos levanta la mano
        clienteUDP.on('message', async (msg, rinfo) => {
            const mensaje = msg.toString();
            if (!resuelto && mensaje.startsWith("TENGO_ESPACIO:")) {
                resuelto = true; // Ya encontramos a uno, ignoramos a los demás
                clearTimeout(timeout);
                
                const partes = mensaje.split(":");
                const ipNodo = partes[1];
                const puertoNodo = partes[2];
                
                console.log(`[P2P] Nodo encontrado en ${ipNodo}. Enviando archivo vía RPC...`);
                
                try {
                    // Nos conectamos directo al nodo ganador y le mandamos el archivo
                    const nodoRemoto = stubify(`http://${ipNodo}:${puertoNodo}`, 'Nodo', ['guardarEnDisco']);
                    const res = await nodoRemoto.guardarEnDisco(nombreArchivo);
                    clienteUDP.close();
                    
                    if (res === 1) resolve({ tipo: 'exito', mensaje: `Guardado en la RED descentralizada (En nodo ${ipNodo}).` });
                    else resolve({ tipo: 'error', mensaje: "Error al guardar en el nodo remoto." });
                } catch (e) {
                    clienteUDP.close();
                    resolve({ tipo: 'error', mensaje: "Fallo la conexión RPC con el nodo." });
                }
            }
>>>>>>> 35773bbdbc5ee6ce9d4c785e716333d7c94f4d2a
        });

<<<<<<< HEAD
buscador.bind(() => {
    buscador.addMembership('231.0.0.1', miIp);
    buscador.setMulticastInterface(miIp);
    buscador.send(Buffer.from("BUSCANDO"), 10000, '231.0.0.1');
=======
        // Gritamos a toda la red preguntando por espacio
        clienteUDP.bind(() => {
            clienteUDP.setBroadcast(true);
            const mensaje = Buffer.from(`QUIEN_TIENE_ESPACIO:${nombreArchivo}`);
            
            // Ojo: Si el hotspot de tu celular bloquea el universal (255.255.255.255), 
            // cambia esto por la IP de Broadcast de tu celular (ej. '172.20.10.255')
            clienteUDP.send(mensaje, 10000, '255.255.255.255', (err) => {
                if (err) console.log("Error al enviar Broadcast.");
            });
        });
    });
}

// Iniciar el cliente web
server.listen(PUERTO_WEB, '0.0.0.0', () => {
    console.log(`==================================================`);
    console.log(`   CLIENTE P2P DESCENTRALIZADO LISTO`);
    console.log(`   navegador: http://localhost:${PUERTO_WEB}`);
    console.log(`==================================================`);
>>>>>>> 35773bbdbc5ee6ce9d4c785e716333d7c94f4d2a
});