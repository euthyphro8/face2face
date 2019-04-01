package somethingspecific.face2face.net


import io.javalin.Javalin;


class SignalClient(){

//    private val connection:WebSocket

    init {
        val app = Javalin.create().staxrt(3000);

        app.ws("/websocket") { ws ->
            ws.onConnect { session -> println("Connected") }
            ws.onMessage { session, message ->
                ("Received: " + message)
                session.remote.sendString("Echo: " + message)
            }
            ws.onClose { session, statusCode, reason -> println("Closed") }
            ws.onError { session, throwable -> println("Errored") }
        }

    }


}