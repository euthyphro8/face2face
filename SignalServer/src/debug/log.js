let output = document.getElementById("console");
let logId = 0;


function log(level, msg) {
     let div = document.createElement('div');
     div.className = level.toString();
     div.id = (logId++).toString();
     div.innerHTML = '[' + level + '] ' + msg;
    output.appendChild(div);
}


log('info', 'Logger initialized.')
