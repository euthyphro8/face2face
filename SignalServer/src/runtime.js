import { SignalServer } from "./com/signalServer";
import { Logger } from "./debug/log";
const config = require('../config.json');

let logger = new Logger(document.getElementById('console'));
let server = new SignalServer(config.socket.port, logger);

