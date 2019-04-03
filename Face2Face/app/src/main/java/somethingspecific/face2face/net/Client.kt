import java.net.URI
import java.net.URISyntaxException

import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft
import org.java_websocket.drafts.Draft_76
import org.java_websocket.framing.Framedata
import org.java_websocket.handshake.ServerHandshake

/** This example demonstrates how to create a websocket connection to a server. Only the most important callbacks are overloaded.  */
class TestClient : WebSocketClient {

    constructor(serverUri: URI, draft: Draft) : super(serverUri, draft) {}

    constructor(serverURI: URI) : super(serverURI) {}

    override fun onOpen(handshakedata: ServerHandshake) {
        send("Hello, it is me. Mario :)")
        println("opened connection")
        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
    }

    override fun onMessage(message: String) {
        println("received: $message")
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        println("Connection closed by " + (if (remote) "remote peer" else "us") + " Code: " + code + " Reason: " + reason)
    }

    override fun onError(ex: Exception) {
        ex.printStackTrace()
        // if the error is fatal then onClose will be called additionally
    }

}