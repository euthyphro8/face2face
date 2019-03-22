
function jify(obj) {
    return JSON.stringify(obj, null, '    ');
}


exports.jify = jify;