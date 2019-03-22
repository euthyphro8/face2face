//Main proc

const ws = require('ws');
const util = require('./../util.js');

let server;

let externOnMessage;
let debug;

function start() {
    server = ws.server('8080');
    server.on('connection', onConnection);
}

function register(onMessageHandler) {
    externOnMessage = onMessageHandler;
}

function enableDebugging(debugFunc) {
    debug = debugFunc;
}

function onConnection(ws) {
    if(debug)
        debug.log('INFO', 'Socket connection received:' + util.jify(ws) + ".");
    ws.on('message', onMessage);
}

function onMessage(msg) {
    if(debug)
        debug.log('INFO', 'Message received:' + util.jify(msg) + ".");
    if(externOnMessage)
        externOnMessage(msg);
}



exports.start = start;
exports.register = regsiter;
exports.enableDebugging = enableDebugging;