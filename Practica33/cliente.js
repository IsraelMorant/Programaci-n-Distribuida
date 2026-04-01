const http = require('http');
const fs = require('fs');
const path = require('path');
const dgram = require('dgram');
const stubify = require('./stubify');

const PUERTO_WEB = 3000;

const server = http.createServer(async (req, res) => {
    if (req.method === 'GET' && req.url === '/') {
        fs.readFile(path.join(__dirname, 'index.html'), (err, data) => {
            if (err) { res.writeHead(500); res.end('Error'); return; }
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
    const dirLocal = './archivos_cliente';
    if (!fs.existsSync(dirLocal)) fs.mkdirSync(dirLocal);
    const archivosLocales = fs.readdirSync(dirLocal);
    const rutaCompleta = `${dirLocal}/${nombre}`;

    // INTENTO LOCAL
    if (archivosLocales.length < 3) {
        if (fs.existsSync(rutaCompleta)) return { tipo: 'info', mensaje: "El archivo ya existe localmente." };
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
        });

        // Gritamos a toda la red preguntando por espacio
        clienteUDP.bind(() => {
            clienteUDP.setBroadcast(true);
            const mensaje = Buffer.from(`QUIEN_TIENE_ESPACIO:${nombreArchivo}`);
            
        
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
});