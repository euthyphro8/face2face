//Renderer proc
const ws = require('ws');
import { stringify } from "../debug/util";
import { clientList } from "./messageFactory";

class SignalServer{
    constructor(port, logger) {
        this.clients = {};
        this.log = function(type, msg) { if(logger) logger.log(type, msg); };

        this.server = ws.server(port);
        this.server.on('connection', onConnection).bind(this);

        
    }

    onConnection(client) {
        client.on('message', function (raw){
            let msg = JSON.parse(raw);
            this.log('debug', 'Message received:' + stringify(msg) + ".");
            switch(msg.type) {
                case 'info':
                    this.log('info', 'Client ' + msg.sender + ' connected.');
                    this.clients[msg.sender] = 
                    {
                        connection: client,
                        streaming: false
                    };
                    let list = [];
                    for(let key in clients) {
                        list.push(key);
                    }
                    let listMsg = clientList(list);
                    this.broadcast(listMsg);
                    break;
                case 'Offer':
                case 'Reply':
                    if((msg.offer || msg.reply) && this.clients[msg.sender])
                        this.clients[msg.sender].streaming = true;
                case 'Ice':
                case 'Close':
                    if(!msg.offer && !msg.reply && !msg.ice && this.clients[msg.sender])
                        this.clients[msg.sender].streaming = false;
                default:
                    if(this.clients[msg.target])
                        this.send(this.clients[msg.target].connection, raw);
                    break;
            }
        }.bind(this));
    
        client.on('close', function (reason) {
            let id;
            for(let key in this.clients)
            {
                if(this.clients[key] && this.clients[key].connection == client) {
                    id = key;
                    delete this.clients[key];
                }
            }
            this.log('warn', 'Client ' + id + ' disconnected.');
        }.bind(this));
    }

    broadcast(msg) {
        for(let key in clients)
        {
            if(clients[key]) {
                send(clients[key].connection, msg);
            }
        }
    }


    send(client, msg) {
        client.send(msg);
    }
}

exports.SignalServer = SignalServer;