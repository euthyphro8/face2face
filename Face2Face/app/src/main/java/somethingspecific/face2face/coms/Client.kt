package somethingspecific.face2face.coms

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import org.webrtc.StatsReport
import somethingspecific.face2face.events.Event
import somethingspecific.face2face.events.EventManager

public class Client(address:String, username:String, email:String) {


    public val ClientListEvent = Event<ArrayList<ClientParameters>>()


    companion object {
        var instance: Client? = null
        fun instance():Client {
            if(instance == null) {
                throw Exception("Client has not be initialized!")
            }
            return instance!!
        }
    }

    private val TAG = "ClientManager"

    private var events: EventManager
    private var signal: SignalClient
    private var stream: StreamClient?
    private var params: ClientParameters

    private var peers: ArrayList<ClientParameters>

    init{
        peers = ArrayList()
        params = ClientParameters(username, email,
            "https://scontent-lax3-1.xx.fbcdn.net/v/t1.0-9/5616975" +
                    "1_2328522497199217_8528485562888224768_n.jpg?_nc_cat=10" +
                    "6&_nc_ht=scontent-lax3-1.xx&oh=af5534402e7907046a72134c12382" +
                    "81c&oe=5D4AF933",
            "Online")
        events = EventManager()
        signal = SignalClient(events, address)
        stream = null//StreamClient(null,  null, null, Events)

        events.OpenEvent += { onOpen() }
        events.MessageEvent += { msg -> onMessage(msg) }
        events.ClosedEvent += { onClose() }

    }

    private fun onOpen() {
        Log.d(TAG, "Client connected!")
        signal.send(MessageFactory.Info(params.email, params.username, params.avatar))
    }

    private fun onMessage(raw: String) {
        Log.d(TAG, "Got message: $raw")
        val msg = JSONObject(raw)
        val type = msg.getString("type")
        when (type) {
            "List" -> onList(msg.getJSONArray("list"))
            "Offer" -> onOffer(msg.getString("target"),
                msg.getJSONObject("offer"))
            "Reply" -> onReply(msg.getString("target"),
                msg.getJSONObject("reply"))
            "Ice" -> onIce(msg.getString("target"),
                msg.getJSONObject("ice"))
        }
    }

    private fun onList(list:JSONArray) {
        peers.clear()
        for (i in 0 until list.length()) {
            val param = list.getJSONObject(i)
            val email = param.getString("id")
            val user = param.getString("user")
            val avatar = param.getString("avatar")
            val status = param.getString("status")
            peers.add(ClientParameters(user, email, avatar, status))
        }
        ClientListEvent(peers)
    }

    private fun onOffer(target:String, offer:JSONObject) {

    }

    private fun onReply(target:String, reply:JSONObject) {

    }

    private fun onIce(target:String, ice:JSONObject) {

    }

    private fun onClose() {
        Log.d(TAG, "Client disconnected!")
        stream?.let {
            //TODO : stream.close()
        }
    }

    //TODO: Implement all of these!
    private fun ConnectedEvent() {}
    private fun DisconnectedEvent() {}
    private fun RtcClosedEvent() {}
    private fun IceConnectedEvent() {}
    private fun IceDisconnectedEvent() {}
    private fun IceCandidateEvent(candidate: IceCandidate){}
    private fun IceCandidateRemovedEvent(candidates: Array<IceCandidate>) {}
    private fun LocalDescriptionEvent(desc: SessionDescription) {}
    private fun RtcStatsReady(stats: Array<StatsReport>) {}
    private fun RtcConnectionError(error: String) {}

}