<p align="center">
    <img src="https://media.giphy.com/media/XHXJuVxMEEl9trucYE/giphy.gif" width="128" height="128" />
<p align="center">

# Face2Face 
###### Josh Hess

---
Face2Face consists of two applications a client android app and a signaling server.

#### Client App
The android application serves as the client side software that allows the video chatting to commence. 
This implementation centers around the use of the android WebRTC API.
The implementation is utilizing the second most recent (built from branch/73) as of writing this.
The android app will also have a websocket interface to communicate with signaling server.

#### Signal Server
The signaling server (for now) is nothing more than client tracking/message forwarding software.
It simply logs the current connections it has available and keeps track of each of their states
(whether actively streaming with another client or currently available to stream). This signaling, as mentioned prior is done via a websocket server. The signaling server is implemented using Electron which is a framework for wrapping
node.js and other libraries for desktop apps. Thus the websocket on the server side is implemented using a javascript api from the
npm package ws.

## Instructions
---
To run the apk you'll need an android phone with 8.0.0+ or API 26+. To build or modify you'll need android studios.
In order to run the signal server without the binaries you need npm + node.js installed and then the run...

```
npm install
nmp start
```
Finally to package and build run...

```
npm run pack
npm run dist
```


## Remotes
---
Github Repo: 
[https://github.com/euthyphro666/face2face](https://github.com/euthyphro666/face2face)

Bitbucket Repo: 
[https://bitbucket.org/somethingspecific/face2face](https://bitbucket.org/somethingspecific/face2face/src/dev/)

## Downloads
---
App & Server **v0.1.0** Github release: 
[https://github.com/euthyphro666/face2face/releases/tag/v0.1.0](https://github.com/euthyphro666/face2face/releases/tag/v0.1.0)

App **v0.1.0** Google Drive download: 
[https://tinyurl.com/rtcf2f](https://tinyurl.com/rtcf2f)

## Libs
---
WebRtc Library:
[https://chromium.googlesource.com/external/webrtc/](https://chromium.googlesource.com/external/webrtc/)
