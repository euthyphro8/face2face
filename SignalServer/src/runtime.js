const { SignalServer } = require("./src/com/signalServer");
const { Logger } = require("./src/debug/log");
const config = require('./config.json');

let logger = new Logger(document.getElementById('console'));
let server = new SignalServer(config.socket.port, logger);

