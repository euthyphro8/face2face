import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import org.webrtc.StatsReport
import somethingspecific.face2face.coms.MessageFactory
import somethingspecific.face2face.coms.SignalClient
import somethingspecific.face2face.coms.StreamClient
import somethingspecific.face2face.events.EventManager

class Client(address:String, id:String) {

    private val TAG = "ClientManager"

    private var events: EventManager
    private var signal: SignalClient
    private var stream: StreamClient?
    private var Id:String = id


    init{
        events = EventManager()
        signal = SignalClient(events, address)
        stream = null//StreamClient(null,  null, null, Events)

        events.OpenEvent += { onOpen() }
        events.MessageEvent += { msg -> onMessage(msg) }
        events.ClosedEvent += { onClose() }

    }

    private fun onOpen() {
        Log.d(TAG, "Client connected!")
        signal.send(MessageFactory.Info(Id))
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
        val arr = ArrayList<String>()
        for (i in 0 until list.length()) {
            arr.add(list.getString(i))
        }
        //Peers
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