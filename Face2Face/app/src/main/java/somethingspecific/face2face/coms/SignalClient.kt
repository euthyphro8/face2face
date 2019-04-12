package somethingspecific.face2face.coms


import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_10
import org.java_websocket.handshake.ServerHandshake
import somethingspecific.face2face.events.Event
import somethingspecific.face2face.events.EventManager
import java.net.URI


class SignalClient(eventManager: EventManager, address:String): WebSocketClient(URI(address), Draft_10()) {

    private val events : EventManager = eventManager

//#region EventListeners

    override fun onMessage(message: String) {
        Log.d("SignalClient", "<- Got Message!")
        events.MessageEvent(message)
    }

    override fun onOpen(handshake: ServerHandshake) {
        Log.d("SignalClient","Got connection!")
        events.OpenEvent()
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        Log.d("SignalClient","Closed connection!")
        events.ClosedEvent()
    }

    override fun onError(ex: Exception) {
        ex.printStackTrace()
    }

//#endregion

}


