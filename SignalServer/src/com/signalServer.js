//Renderer proc
const ws = require('ws');
const { stringify } = require("../debug/util");
const factory = require("./messageFactory");

class SignalServer{
    constructor(port, logger) {
        this.connections = {};
        this.clientsInfo = {};
        this.port = port;
        this.log = function(type, msg) { if(logger) logger.log(type, msg); };
        
        this.connect();
    }

    connect() {
        this.server = new ws.Server({ port: this.port});
        this.server.on('connection', this.onConnection.bind(this));

        this.log('info', "Server started on " + this.port);
    }

    onConnection(client) {
        this.log('info', 'Got connection.');

        client.on('message', function (raw){
            let msg = JSON.parse(raw);
            this.log('trace', 'Message Inbound:' + stringify(msg));
            switch(msg.type) {
                case 'Info':
                    this.log('info', 'Client ' + msg.sender + ' connected.');
                    this.connections[msg.sender] = 
                    {
                        connection: client
                    };
                    this.clientsInfo[msg.sender] = 
                    {
                        email: msg.sender,
                        user: msg.username,
                        avatar: msg.avatar,
                        status: "Online"
                    };
                    let listMsg = factory.list(this.clientsInfo);
                    this.broadcast(listMsg);
                    break;

                case 'Offer':
                case 'Reply':
                    if(this.clientsInfo[msg.sender])
                        this.clientsInfo[msg.sender].status = "Chatting";
                    if(this.connections[msg.target])
                        this.send(this.connections[msg.target].connection, raw);
                    break;

                case 'Ice':
                    if(this.connections[msg.target])
                        this.send(this.connections[msg.target].connection, raw);
                    break;

                case 'Close':
                    if(this.clientsInfo[msg.sender])
                        this.clientsInfo[msg.sender].status = "Online";
                    if(this.connections[msg.target])
                        this.send(this.connections[msg.target].connection, raw);
                    break;

                default:
                    if(this.connections[msg.target])
                        this.send(this.connections[msg.target].connection, raw);
                    break;
            }
        }.bind(this));
    
        client.on('close', function (reason) {
            let id;
            for(let key in this.connections)
            {
                if(this.connections[key] && this.connections[key].connection == client) {
                    id = key;
                    break;
                }
            }
            
            delete this.connections[id];
            delete this.clientsInfo[id];

            this.log('warn', 'Client ' + id + ' disconnected.');
        }.bind(this));
    }

    broadcast(msg) {
        for(let key in this.connections)
        {
            if(this.connections[key]) {
                this.send(this.connections[key].connection, msg);
            }
        }
    }


    send(client, msg) {
        client.send(msg);
        this.log('trace', 'Message Outbound:' + stringify(JSON.parse(msg)));
    }
}

exports.SignalServer = SignalServer;