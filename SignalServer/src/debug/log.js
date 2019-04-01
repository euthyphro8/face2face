


class Logger {
    constructor(element) {
        this.console = element;
        this.logId = 0;
    }

    log(level, msg) {
        let div = document.createElement('div');
        div.className = level.toString();
        div.id = (this.logIdt++).toString();
        div.innerHTML = '[' + level + '] ' + msg;
        this.output.appendChild(div);
   }
}

exports.Logger = Logger;
