//Renderer proc
const ws = require('ws');
const util = require('./../debug/util.js');

let server;
let clients;

function start() {
    clients = {};

    server = ws.server('8080');
    server.on('connection', onConnection);
}

function onConnection(client) {
    debug.log('INFO', 'Socket connection received:' + util.stringify(ws) + ".");

    ws.on('message', (raw) => {
        let msg = JSON.parse(raw);
        debug.log('INFO', 'Message received:' + util.stringify(msg) + ".");
        switch(msg.type) {
            case 'info':
                clients[msg.sender] = 
                {
                    connection: client,
                    streaming: false
                };
                let list = [];
                for(let key in clients) {
                    list.push(key);
                }
                let listMsg = factory.clientList(list);
                broadcast(listMsg);
                break;
            case 'offer':
            case 'reply':
                if((msg.offer || msg.reply) && clients[msg.sender])
                    clients[msg.sender].streaming = true;
            case 'ice':
            default:
                if(clients[msg.target])
                    send(clients[msg.target].connection, raw);
                break;
        }
    });

    ws.on('close', (reason) => {
        debug.log('INFO', 'Client disconnected.');

    });
}

function broadcast(msg) {
    for(let key in clients)
    {
        if(client[key]) {
            send(client[key].connection, msg);
        }
    }
}

function send(client, msg) {
    client.send(msg);
}

function onMessage(msg) {

}