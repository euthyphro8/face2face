

function info(id) {
    let msg = {};
    msg.type = "Info";
    msg.sender = id;
    return msgToString(msg);
}

function list(clients) {
    let msg = {};
    msg.type = "List";
    msg.clients = clients;
    return msgToString(msg);
}

function offer(id, target, description) {
    let msg = {};
    msg.type = "Offer";
    msg.sender = id;
    msg.target = target;
    msg.offer = description;
    return msgToString(msg);
}

/**
 * Distinction between offer and reply is important for servers state tracking.
 */
function reply(id, target, description) {
    let msg = {};
    msg.type = "Reply";
    msg.sender = id;
    msg.target = target;
    msg.reply = description;
    return msgToString(msg);
}

function ice(id, target, candidate) {
    let msg = {};
    msg.type = "Ice";
    msg.sender = id;
    msg.target = target;
    msg.ice = candidate;
    return msgToString(msg);
}

function close(id, target){
    let msg = {};
    msg.type = "Close";
    msg.sender = id;
    msg.target = target;
    return msgToString(msg);
}

function msgToString(msg) {
    return JSON.stringify(msg);
}

exports.info = info;
exports.list = list;
exports.offer = offer;
exports.reply = reply;
exports.ice = ice;
exports.close = close;
exports.toString = msgToString;