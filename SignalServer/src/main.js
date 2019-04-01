
const { app, BrowserWindow } = require('electron');
var fs = require('fs');

let window;
let config;
var server

function onReady () {
    window = new BrowserWindow(
      { 
        width: 800, 
        height: 600,
        backgroundColor: '#2e2e2e',
        title: 'Signal Server'
    });

    window.loadFile('index.html');
    window.on('closed', () => {
        window = null;
    });

    

    window.webContents.devTools
    window.webContents.openDevTools({mode: 'detach'});
    window.moveTop();


    start();
}

function start() {
    loadConfig();

}

async function loadConfig() {
    let raw = fs.readFileSync('file:./../config.json');
    config = JSON.parse(raw);
}

function debug(msg) {
    console.log(msg);
}

app.on('ready', onReady);
app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

app.on('activate', () => {
  if (window === null) {
    createWindow();
  }
});