# Face2Face
### Client/Server Video Chat Application
---
Face2Face consists of two applications, the android app, as well as the signaling server.

## Face2Face App
---
The android application serves as the client side software that allows the video chatting to commence. 
This implementation centers around the use of the android WebRTC API.
The implementation is utilizing the latest (built from branch/71) as of writing this.
The android app will also have a websocket interface to communicate with signaling server.

## Signal Server
---
The signaling server (for now) is nothing more than client tracking/message forwarding software.
It simply logs the current connections it has available and keeps track of each of their states
(whether actively streaming with another client or currently available to stream).
This signaling, as mentioned prior is done via a websocket server.
The signaling server is implemented using Electron which is a framework for wrapping
node.js and other libraries for desktop apps.
Thus the websockets on the server side is implemented using a javascript api from the
npm package ws.