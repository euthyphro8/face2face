
function stringify(obj) {
    return '<pre class="json" script="var(--mainFont);">' + JSON.stringify(obj, null, 2) + '</pre>';
}

exports.stringify = stringify;