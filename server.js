'use strict'

const http = require('http');
const app = require('./app');

const port = normalizePort(process.env.PORT || '3000');
app.set('port', port);

const server = http.createServer(app);

server.listen(port);
server.on('listening', () => console.log(`Server está rodando na porta: ${port}`));
server.on('error', (error) => console.log(`Ocorreu um erro: ${error}`));

function normalizePort(val) {
    const port = parseInt(val, 10)
    if (isNaN(port)) {
        return val;
    }

    if (port >= 0) {
        return port;
    }

    return false;
}