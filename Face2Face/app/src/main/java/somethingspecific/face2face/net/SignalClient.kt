package somethingspecific.face2face.net


import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_10
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONException
import org.json.JSONObject
import java.net.URI


class SignalClient(){

    private val connection:WebSocketClient

    init {
        Log.d("SignalClient", "------------------------------------ Started ------------------------------------ ")
        connection = object : WebSocketClient(URI("http://10.0.2.2:8887"), Draft_10()) {
            override fun onMessage(message: String) {
                Log.d("SignalClient", "<- Got Message!")
                val obj = JSONObject(message)
                val type = obj.getString("type")

                Log.d("SignalClient", message)
            }

            override fun onOpen(handshake: ServerHandshake) {
                Log.d("SignalClient","Got connection!")

                val obj = JSONObject()
                obj.put("type", "ClientInfo")
                obj.put("sender", "SENDER_08")
                val message = obj.toString()
                //send message
                connection.send(message)
            }

            override fun onClose(code: Int, reason: String, remote: Boolean) {
                Log.d("SignalClient","closeds connection")
            }

            override fun onError(ex: Exception) {
                ex.printStackTrace()
            }

        }

        connection.connect()

    }


}